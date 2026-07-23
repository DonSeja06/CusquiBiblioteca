package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.exception.ResourceNotFoundException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.MaterialRepository;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import cusquiBiblioteca.com.example.demo.rules.ReglaPrestamo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;
    private final List<ReglaPrestamo> reglasPrestamo;

    public PrestamoService(PrestamoRepository prestamoRepository, 
                           MaterialRepository materialRepository,
                           UsuarioRepository usuarioRepository,
                           List<ReglaPrestamo> reglasPrestamo) {
        this.prestamoRepository = prestamoRepository;
        this.materialRepository = materialRepository;
        this.usuarioRepository = usuarioRepository;
        this.reglasPrestamo = reglasPrestamo;
    }
    
    @Transactional
    public void solicitarPrestamo(Usuario usuario, Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("El material solicitado no existe."));

        // Validar todas las reglas de negocio (OOP y Polimorfismo)
        for (ReglaPrestamo regla : reglasPrestamo) {
            regla.validar(usuario, material);
        }

        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setUsuario(usuario);
        nuevoPrestamo.setMaterial(material);
        nuevoPrestamo.setFechaPrestamo(LocalDateTime.now());
        nuevoPrestamo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(material.diasPrestamo()));
        nuevoPrestamo.setEstado("ACTIVO");

        material.setDisponible(false);

        materialRepository.save(material);
        prestamoRepository.save(nuevoPrestamo);
    }
    
    @Transactional
    public boolean devolverPrestamo(Usuario usuario, Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("El registro de préstamo no existe."));

        if (!prestamo.getUsuario().getId().equals(usuario.getId())) {
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

        boolean conRetraso = diasRetraso > 0;
        if (conRetraso) {
            float costoPorDia = 2.0f;
            float multaGenerada = diasRetraso * costoPorDia;

            Usuario usuarioDb = prestamo.getUsuario();
            usuarioDb.setMultaAcumulada(usuarioDb.getMultaAcumulada() + multaGenerada);
            usuarioRepository.save(usuarioDb);
            // Actualizamos la instancia actual para que el controlador tenga el estado correcto si lo necesita
            usuario.setMultaAcumulada(usuarioDb.getMultaAcumulada()); 
        }

        Material material = prestamo.getMaterial();
        material.setDisponible(true);

        materialRepository.save(material);
        prestamoRepository.save(prestamo);
        
        return conRetraso;
    }

    public void auditarPrestamosPorEstado(String estado) {
        System.out.println("--- Auditoría de Préstamos: " + estado.toUpperCase() + " ---");
        prestamoRepository.findAll().stream()
                .filter(prestamo -> prestamo.getEstado().equalsIgnoreCase(estado))
                .forEach(prestamo -> System.out.println(
                        "Préstamo ID: " + prestamo.getId() + 
                        " | Usuario: " + prestamo.getUsuario().getNombre() + 
                        " | Material: " + prestamo.getMaterial().getNombre()
                ));
    }

    public List<String> obtenerCorreosParaNotificacionVencimiento() {
        return prestamoRepository.findAll().stream()
                .filter(prestamo -> prestamo.getEstado().equalsIgnoreCase("VENCIDO"))
                .filter(prestamo -> prestamo.getUsuario() != null) // <-- Evita NullPointerException si no hay usuario
                .map(prestamo -> prestamo.getUsuario().getCorreo())
                .filter(correo -> correo != null) // <-- Evita correos nulos
                .distinct() 
                .collect(Collectors.toList());
    }

    public List<Prestamo> obtenerListaPrestamosPorEstado(String estado) {
        return prestamoRepository.findAll().stream()
                .filter(prestamo -> prestamo.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }
}