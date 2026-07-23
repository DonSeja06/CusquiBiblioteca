package cusquiBiblioteca.com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cusquiBiblioteca.com.example.demo.DTO.RecomendacionLibroDTO;
import cusquiBiblioteca.com.example.demo.DTO.RecomendacionRevistaDTO;
import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.repository.RevistaRepository;

@Service
public class RecomendacionesService {

    private final LibroRepository libroRepository;
    private final RevistaRepository revistaRepository;
    private final PrestamoRepository prestamoRepository; 

    public RecomendacionesService(LibroRepository libroRepository, 
                                  RevistaRepository revistaRepository, 
                                  PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.revistaRepository = revistaRepository;
        this.prestamoRepository = prestamoRepository;
    }

   
    public RecomendacionLibroDTO obtenerRecomendacionesLibrosParaUsuario(Long usuarioId) {
        List<Prestamo> historialUsuario = obtenerHistorialPorUsuario(usuarioId);

        List<Libro> librosPrestados = historialUsuario.stream()
        .map(Prestamo::getMaterial)
        .filter(material -> material != null && material instanceof Libro)
        .map(material -> (Libro) material)
        .collect(Collectors.toList());

        if (librosPrestados.isEmpty()) {
            return generarRecomendacionesLibrosLogica("Desconocido", "General", 200, null);
        }

        String categoriaFavorita = calcularMasRepetido(librosPrestados.stream().map(Material::getCategoria).collect(Collectors.toList()), "General");
        String autorMasLeido = calcularMasRepetido(librosPrestados.stream().map(Material::getAutor).collect(Collectors.toList()), "Desconocido");
        
        int paginasPreferencia = (int) librosPrestados.stream()
                .mapToInt(Libro::getCantidadPaginas)
                .average()
                .orElse(200);

        Long ultimoLibroId = librosPrestados.get(librosPrestados.size() - 1).getId();

        return generarRecomendacionesLibrosLogica(autorMasLeido, categoriaFavorita, paginasPreferencia, ultimoLibroId);
    }

   
    public RecomendacionRevistaDTO obtenerRecomendacionesRevistasParaUsuario(Long usuarioId) {
        List<Prestamo> historialUsuario = obtenerHistorialPorUsuario(usuarioId);

        List<Revista> revistasPrestadas = historialUsuario.stream()
        .map(Prestamo::getMaterial)
        .filter(material -> material != null && material instanceof Revista) 
        .map(material -> (Revista) material)
        .collect(Collectors.toList());

        if (revistasPrestadas.isEmpty()) {
            return generarRecomendacionesRevistasLogica("General", "Desconocida", 1, null);
        }

        String categoriaFavorita = calcularMasRepetido(revistasPrestadas.stream().map(Material::getCategoria).collect(Collectors.toList()), "General");
        
        String editorialFavorita = calcularMasRepetido(revistasPrestadas.stream().map(Material::getAutor).collect(Collectors.toList()), "Desconocida");

        Revista ultimaRevista = revistasPrestadas.get(revistasPrestadas.size() - 1);
        int ultimoVolumen = ultimaRevista.getVolumen();
        Long ultimaRevistaId = ultimaRevista.getId();

        return generarRecomendacionesRevistasLogica(categoriaFavorita, editorialFavorita, ultimoVolumen, ultimaRevistaId);
    }


    private List<Prestamo> obtenerHistorialPorUsuario(Long usuarioId) {
        return prestamoRepository.findAll().stream()
                .filter(prestamo -> prestamo.getUsuario().getId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    private String calcularMasRepetido(List<String> elementos, String valorPorDefecto) {
        return elementos.stream()
                .filter(e -> e != null)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(valorPorDefecto);
    }

    private RecomendacionLibroDTO generarRecomendacionesLibrosLogica(String autorMasLeido, String categoriaFavorita, int paginasPreferencia, Long ultimoLibroId) {
        RecomendacionLibroDTO dto = new RecomendacionLibroDTO();
        dto.setPorAutorYGenero(new ArrayList<>());
        dto.setPorPaginasSimilares(new ArrayList<>());
        dto.setOtrasSugerencias(new ArrayList<>());

        libroRepository.findAll().stream()
                .filter(libro -> ultimoLibroId == null || !libro.getId().equals(ultimoLibroId))
                .forEach(libro -> {
                    boolean mismoAutor = libro.getAutor() != null && libro.getAutor().equalsIgnoreCase(autorMasLeido);
                    boolean mismaCategoria = libro.getCategoria() != null && libro.getCategoria().equalsIgnoreCase(categoriaFavorita);
                    boolean paginasSimilares = Math.abs(libro.getCantidadPaginas() - paginasPreferencia) <= 100;

                    if (mismoAutor && mismaCategoria) {
                        dto.getPorAutorYGenero().add(libro);
                    } else if (mismaCategoria && paginasSimilares) {
                        dto.getPorPaginasSimilares().add(libro);
                    } else if (mismoAutor || mismaCategoria) {
                        dto.getOtrasSugerencias().add(libro);
                    }
                });
        return dto;
    }

    private RecomendacionRevistaDTO generarRecomendacionesRevistasLogica(String categoriaFavorita, String editorialFavorita, int ultimoVolumen, Long ultimaRevistaId) {
        RecomendacionRevistaDTO dto = new RecomendacionRevistaDTO();
        dto.setSiguienteVolumen(new ArrayList<>());
        dto.setMismaEditorial(new ArrayList<>());
        dto.setOtrasRevistasCategorias(new ArrayList<>());

        revistaRepository.findAll().stream()
                .filter(revista -> ultimaRevistaId == null || !revista.getId().equals(ultimaRevistaId))
                .forEach(revista -> {
                    boolean mismaCategoria = revista.getCategoria() != null && revista.getCategoria().equalsIgnoreCase(categoriaFavorita);
                    boolean mismaEditorial = revista.getAutor() != null && revista.getAutor().equalsIgnoreCase(editorialFavorita);
                    boolean esVolumenAdyacente = revista.getVolumen() == (ultimoVolumen + 1) || revista.getVolumen() == (ultimoVolumen - 1);

                    if (mismaCategoria && esVolumenAdyacente) {
                        dto.getSiguienteVolumen().add(revista);
                    } else if (mismaEditorial) {
                        dto.getMismaEditorial().add(revista);
                    } else if (mismaCategoria) {
                        dto.getOtrasRevistasCategorias().add(revista);
                    }
                });
        return dto;
    }
}