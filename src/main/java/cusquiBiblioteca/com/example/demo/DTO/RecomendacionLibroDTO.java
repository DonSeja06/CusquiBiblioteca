package cusquiBiblioteca.com.example.demo.DTO;

import cusquiBiblioteca.com.example.demo.model.Libro;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomendacionLibroDTO {
        private List<Libro> porAutorYGenero;
        private List<Libro> porPaginasSimilares;
        private List<Libro> otrasSugerencias;

    
}
