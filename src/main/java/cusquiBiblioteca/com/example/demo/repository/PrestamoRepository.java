package cusquiBiblioteca.com.example.demo.repository;

import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    
    // Spring Boot buscará todos los préstamos vinculados a un usuario específico
    List<Prestamo> findByUsuario(Usuario usuario);
    
}