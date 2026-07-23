package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.MaterialRepository;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final PrestamoRepository prestamoRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(PrestamoRepository prestamoRepository, MaterialRepository materialRepository, UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.materialRepository = materialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Map<String, Long> obtenerMaterialesMasPrestados() {
        return prestamoRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getMaterial().getNombre(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> obtenerUsuariosConMasPrestamos() {
        return prestamoRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getUsuario().getNombre(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    public List<Material> obtenerMaterialesNuncaPrestados() {
        List<Long> materialesPrestadosIds = prestamoRepository.findAll().stream()
                .map(p -> p.getMaterial().getId())
                .distinct()
                .collect(Collectors.toList());

        return materialRepository.findAll().stream()
                .filter(m -> !materialesPrestadosIds.contains(m.getId()))
                .collect(Collectors.toList());
    }

    public double obtenerMontoTotalMultas() {
        return usuarioRepository.findAll().stream()
                .mapToDouble(Usuario::getMultaAcumulada)
                .sum();
    }
    
    public Map<String, Long> obtenerCategoriasMasPopulares() {
        return prestamoRepository.findAll().stream()
                .map(Prestamo::getMaterial)
                .collect(Collectors.groupingBy(Material::getCategoria, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
