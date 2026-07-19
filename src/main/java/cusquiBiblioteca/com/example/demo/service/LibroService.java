package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

   
    public void buscarYMostrarLibrosPorAutor(String autor) {
        System.out.println("Resultados para el autor: " + autor);

        libroRepository.findAll().stream()
                .filter(libro -> libro.getAutor() != null && libro.getAutor().equalsIgnoreCase(autor)) // <-- Protección añadida
                .forEach(libro -> System.out.println("- " + libro.getNombre() + " (Categoría: " + libro.getCategoria() + ")"));
    }

  
    public List<String> filtrarNombresPorCategoria(String categoria) {
        return libroRepository.findAll().stream()
                .filter(libro -> libro.getCategoria() != null && libro.getCategoria().equalsIgnoreCase(categoria))
                .map(Libro::getNombre)
                .collect(Collectors.toList());
    }

   
    public String obtenerResumenLibrosDisponibles() {
        return libroRepository.findAll().stream()
                .filter(Libro::isDisponible)
                .map(Libro::getNombre)
                .reduce((nombre1, nombre2) -> nombre1 + ", " + nombre2)
                .orElse("Actualmente no hay libros disponibles en la biblioteca.");
    }
}