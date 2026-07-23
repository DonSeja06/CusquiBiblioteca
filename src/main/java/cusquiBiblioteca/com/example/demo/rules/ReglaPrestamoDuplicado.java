package cusquiBiblioteca.com.example.demo.rules;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import org.springframework.stereotype.Component;

@Component
public class ReglaPrestamoDuplicado implements ReglaPrestamo {
    
    private final PrestamoRepository prestamoRepository;

    public ReglaPrestamoDuplicado(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    public void validar(Usuario usuario, Material material) {
        boolean yaLoTiene = prestamoRepository.findByUsuario(usuario).stream()
                .anyMatch(p -> "ACTIVO".equals(p.getEstado()) && p.getMaterial().getId().equals(material.getId()));
                
        if (yaLoTiene) {
            throw new BusinessRuleException("Ya tienes un préstamo activo para este material.");
        }
    }
}
