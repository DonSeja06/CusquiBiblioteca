package cusquiBiblioteca.com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cusquiBiblioteca.com.example.demo.model.Libro;


public interface LibroRepository extends JpaRepository<Libro,Long>{
    Optional<Libro> findByIsbn(String isbn);
    Optional<Libro> findByNombre(String nombre);
    Optional<Libro> findByEditorial(String editorial);
    Optional<Libro> findByAutor(String autor);
}
