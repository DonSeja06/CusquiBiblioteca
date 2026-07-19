package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.exception.ResourceNotFoundException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoController(PrestamoRepository prestamoRepository, MaterialRepository materialRepository,
            UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.materialRepository = materialRepository;
        this.usuarioRepository = usuarioRepository;
    }

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

    @PostMapping("/solicitar-prestamo")
    public String solicitarPrestamo(@RequestParam("materialId") Long materialId, HttpSession session) {

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        if (usuarioLogeado.getMultaAcumulada() > 0) {
            throw new BusinessRuleException("No puedes solicitar préstamos si tienes multas pendientes.");
        }

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("El material solicitado no existe."));

        if (!material.isDisponible()) {
            throw new BusinessRuleException("El material no se encuentra disponible actualmente.");
        }

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


    @PostMapping("/devolver-prestamo")
    public String devolverPrestamo(@RequestParam("prestamoId") Long prestamoId, HttpSession session) {

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("El registro de préstamo no existe."));

        if (!prestamo.getUsuario().getId().equals(usuarioLogeado.getId())) {
            throw new BusinessRuleException("No tienes permisos para devolver este préstamo.");
        }
        
        if (!"ACTIVO".equals(prestamo.getEstado())) {
            throw new BusinessRuleException("Este préstamo ya ha sido devuelto o se encuentra inactivo.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        prestamo.setFechaDevolucionReal(ahora);
        prestamo.setEstado("DEVUELTO");

        long diasRetraso = ChronoUnit.DAYS.between(
                prestamo.getFechaDevolucionEsperada().toLocalDate(),
                ahora.toLocalDate());

        if (diasRetraso > 0) {
            float costoPorDia = 2.0f;
            float multaGenerada = diasRetraso * costoPorDia;

            Usuario usuario = prestamo.getUsuario();
            usuario.setMultaAcumulada(usuario.getMultaAcumulada() + multaGenerada);

            usuarioRepository.save(usuario);
            session.setAttribute("usuarioLogeado", usuario);
        }

        Material material = prestamo.getMaterial();
        material.setDisponible(true);

        materialRepository.save(material);
        prestamoRepository.save(prestamo);

        if (diasRetraso > 0) {
            return "redirect:/mis-prestamos?exito=devuelto&retraso=true";
        }

        return "redirect:/mis-prestamos?exito=devuelto";
    }
}