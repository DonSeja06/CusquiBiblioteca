package cusquiBiblioteca.com.example.demo.repository;

import cusquiBiblioteca.com.example.demo.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    // Busca en título, autor o categoría
    @Query("SELECT m FROM Material m WHERE " +
           "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(m.autor) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(m.categoria) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Material> buscarPorTermino(@Param("termino") String termino, Pageable pageable);
    
    // NUEVO: Busca exactamente por la Clase (Libro)
    @Query("SELECT m FROM Material m WHERE TYPE(m) = Libro")
    Page<Material> findAllLibros(Pageable pageable);

    // NUEVO: Busca exactamente por la Clase (Revista)
    @Query("SELECT m FROM Material m WHERE TYPE(m) = Revista")
    Page<Material> findAllRevistas(Pageable pageable);
}