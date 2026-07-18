package cusquiBiblioteca.com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cusquiBiblioteca.com.example.demo.DTO.RecomendacionLibroDTO;
import cusquiBiblioteca.com.example.demo.DTO.RecomendacionRevistaDTO;
import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import cusquiBiblioteca.com.example.demo.repository.RevistaRepository;


@Service
public class RecomendacionesService {
    private final LibroRepository libroRepository;
    private final RevistaRepository revistaRepository;

    public RecomendacionesService(LibroRepository libroRepository, RevistaRepository revistaRepository) {
        this.libroRepository = libroRepository;
        this.revistaRepository = revistaRepository;
    }

    /**
     * RECOMENDACIONES DE LIBROS POR CATEGORÍAS (DTO)
     */
    public RecomendacionLibroDTO obtenerRecomendacionesLibros(String autorMasLeido, String categoriaFavorita, int paginasPreferencia, Long ultimoLibroId) {
        List<Libro> todosLosLibros = libroRepository.findAll();
        
        // Instanciamos nuestro contenedor
        RecomendacionLibroDTO dto = new RecomendacionLibroDTO();
        dto.setPorAutorYGenero(new ArrayList<>());
        dto.setPorPaginasSimilares(new ArrayList<>());
        dto.setOtrasSugerencias(new ArrayList<>());

        for (Libro libro : todosLosLibros) {
            if (libro.getId().equals(ultimoLibroId)) continue; // Ignorar el ya leído

            boolean mismoAutor = libro.getAutor().equalsIgnoreCase(autorMasLeido);
            boolean mismaCategoria = libro.getCategoria().equalsIgnoreCase(categoriaFavorita);
            boolean paginasSimilares = Math.abs(libro.getCantidadPaginas() - paginasPreferencia) <= 100;

            // Condicional 1: Match Perfecto (Autor y Categoría)
            if (mismoAutor && mismaCategoria) {
                dto.getPorAutorYGenero().add(libro);
            } 
            // Condicional 2: Match por tamaño de lectura
            else if (mismaCategoria && paginasSimilares) {
                dto.getPorPaginasSimilares().add(libro);
            } 
            // Condicional 3: Solo coincide una de las dos cosas sueltas
            else if (mismoAutor || mismaCategoria) {
                dto.getOtrasSugerencias().add(libro);
            }
        }
        return dto;
    }

    /**
     * RECOMENDACIONES DE REVISTAS POR CATEGORÍAS (DTO)
     */
    public RecomendacionRevistaDTO obtenerRecomendacionesRevistas(String categoriaFavorita, String editorialFavorita, int ultimoVolumen, Long ultimaRevistaId) {
        List<Revista> todasLasRevistas = revistaRepository.findAll();

        RecomendacionRevistaDTO dto = new RecomendacionRevistaDTO();
        dto.setSiguienteVolumen(new ArrayList<>());
        dto.setMismaEditorial(new ArrayList<>());
        dto.setOtrasRevistasCategorias(new ArrayList<>());

        for (Revista revista : todasLasRevistas) {
            if (revista.getId().equals(ultimaRevistaId)) continue;

            boolean mismaCategoria = revista.getCategoria().equalsIgnoreCase(categoriaFavorita);
            boolean mismaEditorial = revista.getAutor().equalsIgnoreCase(editorialFavorita);
            boolean esVolumenAdyacente = revista.getVolumen() == (ultimoVolumen + 1) || revista.getVolumen() == (ultimoVolumen - 1);

            // Condicional 1: Continuidad (Misma categoría y volumen anterior o posterior)
            if (mismaCategoria && esVolumenAdyacente) {
                dto.getSiguienteVolumen().add(revista);
            } 
            // Condicional 2: Fidelidad de Marca (Misma editorial/marca)
            else if (mismaEditorial) {
                dto.getMismaEditorial().add(revista);
            } 
            // Condicional 3: Solo comparte el tema/categoría general
            else if (mismaCategoria) {
                dto.getOtrasRevistasCategorias().add(revista);
            }
        }
        return dto;
    }

}