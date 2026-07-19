package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import cusquiBiblioteca.com.example.demo.repository.RevistaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Inyectamos los repositorios para poder interactuar con la base de datos
    private final LibroRepository libroRepository;
    private final RevistaRepository revistaRepository;

    public AdminController(LibroRepository libroRepository, RevistaRepository revistaRepository) {
        this.libroRepository = libroRepository;
        this.revistaRepository = revistaRepository;
    }

    // --- VISTAS GET (Muestran los formularios) ---

    @GetMapping("/agregar-libro")
    public String mostrarFormularioLibro(Model model) {
        model.addAttribute("libro", new Libro());
        return "formulario-libro"; 
    }

    @GetMapping("/agregar-revista")
    public String mostrarFormularioRevista(Model model) {
        model.addAttribute("revista", new Revista());
        return "formulario-revista";
    }

    // --- MÉTODOS POST (Guardan en la base de datos) ---

    @PostMapping("/guardar-libro")
    public String guardarLibro(@ModelAttribute Libro libro) {
        // Aquí es donde la magia ocurre: se guarda en la BD (MySQL)
        libroRepository.save(libro);
        
        // Redirigimos al formulario limpio de nuevo (puedes agregar un parámetro ?exito para mostrar un mensaje)
        return "redirect:/admin/agregar-libro?exito";
    }

    @PostMapping("/guardar-revista")
    public String guardarRevista(@ModelAttribute Revista revista) {
        // Se guarda en la BD
        revistaRepository.save(revista);
        
        // Redirigimos de vuelta
        return "redirect:/admin/agregar-revista?exito";
    }
}