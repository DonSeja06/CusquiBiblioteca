package cusquiBiblioteca.com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // cuando algo no se encuentra (Libro, Revista, etc.)
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("mensajeError", ex.getMessage());
        return "error/404"; 
    }

    // violaciones de reglas de negocio 
    @ExceptionHandler(BusinessRuleException.class)
    public String handleBusinessRules(BusinessRuleException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "redirect:/catalogo?errorCustom=" + ex.getMessage();
    }

    //errores de base de datos 
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("error", "Error de consistencia: El registro ya existe o viola restricciones de la base de datos.");
        return "error/error-general"; 
    }

    // cuando manipulan los parámetros de la URL 
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public String handleTypeMismatch(Exception ex, Model model) {
        model.addAttribute("error", "Los datos enviados en la dirección URL son inválidos.");
        return "error/error-general";
    }
}