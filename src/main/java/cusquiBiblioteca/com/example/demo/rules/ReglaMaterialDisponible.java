package cusquiBiblioteca.com.example.demo.rules;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ReglaMaterialDisponible implements ReglaPrestamo {
    @Override
    public void validar(Usuario usuario, Material material) {
        if (!material.isDisponible()) {
            throw new BusinessRuleException("El material no se encuentra disponible actualmente.");
        }
    }
}
