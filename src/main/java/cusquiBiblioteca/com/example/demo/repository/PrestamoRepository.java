package cusquiBiblioteca.com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cusquiBiblioteca.com.example.demo.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo,Long>{
    List<Prestamo> findByEstado(String estado);
}
