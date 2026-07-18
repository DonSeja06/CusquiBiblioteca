package cusquiBiblioteca.com.example.demo.DTO;

import cusquiBiblioteca.com.example.demo.model.Revista;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomendacionRevistaDTO {
    private List<Revista> siguienteVolumen;
    private List<Revista> mismaEditorial;
    private List<Revista> otrasRevistasCategorias;

}
