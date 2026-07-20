package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import cusquiBiblioteca.com.example.demo.repository.RevistaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LibroRepository libroRepository;
    private final RevistaRepository revistaRepository;

    public AdminController(LibroRepository libroRepository, RevistaRepository revistaRepository) {
        this.libroRepository = libroRepository;
        this.revistaRepository = revistaRepository;
    }

    @GetMapping("/agregar-libro")
    public String mostrarFormularioLibro(Model model) {
        model.addAttribute("libro", new Libro());
        
        model.addAttribute("libros", libroRepository.findAll()); 
        return "formulario-libro";
    }

    @GetMapping("/editar-libro/{id}")
    public String editarLibro(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElse(new Libro());
        model.addAttribute("libro", libro);
        model.addAttribute("libros", libroRepository.findAll());
        return "formulario-libro";
    }

    @PostMapping("/guardar-libro")
    public String guardarLibro(@ModelAttribute Libro libro) {
        libroRepository.save(libro);
        return "redirect:/admin/agregar-libro?exito";
    }

    @PostMapping("/eliminar-libro")
    public String eliminarLibro(@RequestParam Long id) {
        libroRepository.deleteById(id);
        return "redirect:/admin/agregar-libro?eliminado";
    }

    @GetMapping("/agregar-revista")
    public String mostrarFormularioRevista(Model model) {
        model.addAttribute("revista", new Revista());
        model.addAttribute("revistas", revistaRepository.findAll());
        return "formulario-revista";
    }

    @GetMapping("/editar-revista/{id}")
    public String editarRevista(@PathVariable Long id, Model model) {
        Revista revista = revistaRepository.findById(id).orElse(new Revista());
        model.addAttribute("revista", revista);
        model.addAttribute("revistas", revistaRepository.findAll());
        return "formulario-revista";
    }

    @PostMapping("/guardar-revista")
    public String guardarRevista(@ModelAttribute Revista revista) {
        revistaRepository.save(revista);
        return "redirect:/admin/agregar-revista?exito";
    }

    @PostMapping("/eliminar-revista")
    public String eliminarRevista(@RequestParam Long id) {
        revistaRepository.deleteById(id);
        return "redirect:/admin/agregar-revista?eliminado";
    }
}