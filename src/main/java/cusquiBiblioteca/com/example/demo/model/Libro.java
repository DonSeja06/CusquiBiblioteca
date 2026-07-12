package cusquiBiblioteca.com.example.demo.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Libro extends Material{
    private String isbn;
    private String editorial;
    private int cantidadPaginas;

    @Override
    public int diasPrestamo(){
        return 14;
    }
}
