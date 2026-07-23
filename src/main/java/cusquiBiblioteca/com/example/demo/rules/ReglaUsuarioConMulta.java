package cusquiBiblioteca.com.example.demo.rules;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ReglaUsuarioConMulta implements ReglaPrestamo {
    @Override
    public void validar(Usuario usuario, Material material) {
        if (usuario.getMultaAcumulada() > 0) {
            throw new BusinessRuleException("No puedes solicitar préstamos si tienes multas pendientes.");
        }
    }
}
