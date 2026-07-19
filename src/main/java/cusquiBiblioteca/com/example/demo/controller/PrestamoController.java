package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.MaterialRepository;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; // Importante para calcular días de diferencia
import java.util.List;
import java.util.Optional;

@Controller
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository; // Añadido para poder guardar la multa

    public PrestamoController(PrestamoRepository prestamoRepository, MaterialRepository materialRepository, UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.materialRepository = materialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- VISTA: MIS PRÉSTAMOS ---
    @GetMapping("/mis-prestamos")
    public String verMisPrestamos(HttpSession session, Model model) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        List<Prestamo> misPrestamos = prestamoRepository.findByUsuario(usuarioLogeado);
        model.addAttribute("prestamos", misPrestamos);
        
        return "mis-prestamos"; 
    }

    // --- ACCIÓN: SOLICITAR PRÉSTAMO ---
    @PostMapping("/solicitar-prestamo")
    public String solicitarPrestamo(@RequestParam("materialId") Long materialId, HttpSession session) {
        
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        // ¡EL CANDADO DE MULTAS!
        // Verificamos si el usuario tiene más de 0 de deuda
        if (usuarioLogeado.getMultaAcumulada() > 0) {
            // Lo regresamos al catálogo con un error específico
            return "redirect:/catalogo?error=multa";
        }

        Optional<Material> materialOpt = materialRepository.findById(materialId);

        if (materialOpt.isPresent() && materialOpt.get().isDisponible()) {
            Material material = materialOpt.get();
            
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setUsuario(usuarioLogeado);
            nuevoPrestamo.setMaterial(material);
            
            nuevoPrestamo.setFechaPrestamo(LocalDateTime.now());
            nuevoPrestamo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(material.diasPrestamo()));
            nuevoPrestamo.setEstado("ACTIVO"); 
            
            material.setDisponible(false);
            
            materialRepository.save(material);
            prestamoRepository.save(nuevoPrestamo);
            
            return "redirect:/mis-prestamos";
        }

        return "redirect:/catalogo?error=no-disponible";
    }
    // --- ACCIÓN: DEVOLVER PRÉSTAMO Y CALCULAR MULTA ---
    @PostMapping("/devolver-prestamo")
    public String devolverPrestamo(@RequestParam("prestamoId") Long prestamoId, HttpSession session) {
        
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(prestamoId);

        if (prestamoOpt.isPresent()) {
            Prestamo prestamo = prestamoOpt.get();

            if (prestamo.getUsuario().getId().equals(usuarioLogeado.getId()) && "ACTIVO".equals(prestamo.getEstado())) {
                
                LocalDateTime ahora = LocalDateTime.now();
                prestamo.setFechaDevolucionReal(ahora);
                prestamo.setEstado("DEVUELTO");

                // --- LÓGICA DE MULTAS ---
                // Calculamos la diferencia en días usando LocalDate para ser exactos con el calendario
                long diasRetraso = ChronoUnit.DAYS.between(
                        prestamo.getFechaDevolucionEsperada().toLocalDate(), 
                        ahora.toLocalDate()
                );
                
                // Si la diferencia es mayor a 0, significa que se pasó de la fecha
                if (diasRetraso > 0) {
                    float costoPorDia = 2.0f; // S/ 2.00 por cada día de retraso (puedes cambiarlo)
                    float multaGenerada = diasRetraso * costoPorDia;
                    
                    Usuario usuario = prestamo.getUsuario();
                    usuario.setMultaAcumulada(usuario.getMultaAcumulada() + multaGenerada);
                    
                    // Guardamos el usuario con su nueva deuda
                    usuarioRepository.save(usuario); 
                    
                    // Actualizamos la sesión para que el sistema reconozca la deuda de inmediato
                    session.setAttribute("usuarioLogeado", usuario);
                }
                // --- FIN LÓGICA MULTAS ---

                Material material = prestamo.getMaterial();
                material.setDisponible(true);

                materialRepository.save(material);
                prestamoRepository.save(prestamo);

                // Si hubo retraso, enviamos un parámetro especial "?retraso=true"
                if (diasRetraso > 0) {
                    return "redirect:/mis-prestamos?exito=devuelto&retraso=true";
                }

                // Devolución a tiempo
                return "redirect:/mis-prestamos?exito=devuelto";
            }
        }

        return "redirect:/mis-prestamos?error=true";
    }
}