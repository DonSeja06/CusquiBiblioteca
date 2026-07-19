package cusquiBiblioteca.com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    
    Optional<Usuario> findByCorreo(String correo);
}
