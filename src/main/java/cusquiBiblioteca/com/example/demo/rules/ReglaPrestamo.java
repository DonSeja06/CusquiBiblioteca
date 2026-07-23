package cusquiBiblioteca.com.example.demo.rules;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Usuario;

public interface ReglaPrestamo {
    void validar(Usuario usuario, Material material);
}
