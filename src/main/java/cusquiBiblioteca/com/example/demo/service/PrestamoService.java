package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
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
                .map(prestamo -> prestamo.getUsuario().getCorreo())
                .distinct() 
                .collect(Collectors.toList());
    }

    public List<Prestamo> obtenerListaPrestamosPorEstado(String estado) {
        return prestamoRepository.findAll().stream()
                .filter(prestamo -> prestamo.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

}