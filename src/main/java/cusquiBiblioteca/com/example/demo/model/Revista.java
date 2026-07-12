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
public class Revista extends Material{
    private int volumen;
    private int edicion;

    @Override
    public int diasPrestamo(){
        return 7;
    }
}
