package cusquiBiblioteca.com.example.demo.rules;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import org.springframework.stereotype.Component;

@Component
public class ReglaLimitePrestamos implements ReglaPrestamo {
    
    private final PrestamoRepository prestamoRepository;
    private static final int LIMITE_PRESTAMOS_ACTIVOS = 3;

    public ReglaLimitePrestamos(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    public void validar(Usuario usuario, Material material) {
        long prestamosActivos = prestamoRepository.findByUsuario(usuario).stream()
                .filter(p -> "ACTIVO".equals(p.getEstado()))
                .count();
                
        if (prestamosActivos >= LIMITE_PRESTAMOS_ACTIVOS) {
            throw new BusinessRuleException("Has alcanzado el límite máximo de " + LIMITE_PRESTAMOS_ACTIVOS + " préstamos activos.");
        }
    }
}
