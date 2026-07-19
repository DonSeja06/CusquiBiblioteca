package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final LibroRepository libroRepository;

    public HomeController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Libro> todosLosLibros = libroRepository.findAll();

        int limit = Math.min(todosLosLibros.size(), 5);
        List<Libro> librosDestacados = todosLosLibros.subList(0, limit);

        model.addAttribute("librosDestacados", librosDestacados);
        return "index";
    }
}