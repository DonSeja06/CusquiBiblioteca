package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.DTO.RecomendacionLibroDTO;
import cusquiBiblioteca.com.example.demo.DTO.RecomendacionRevistaDTO;
import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.exception.ResourceNotFoundException;
import cusquiBiblioteca.com.example.demo.model.Libro;
import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.LibroRepository;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import cusquiBiblioteca.com.example.demo.service.RecomendacionesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;
    private final RecomendacionesService recomendacionesService;

    public HomeController(LibroRepository libroRepository, UsuarioRepository usuarioRepository, PrestamoRepository prestamoRepository, RecomendacionesService recomendacionesService) {
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
        this.recomendacionesService = recomendacionesService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        
        List<Libro> librosDestacados = libroRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("librosDestacados", librosDestacados);

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");

        if (usuarioLogeado != null) {
            Usuario usuarioBD = usuarioRepository.findById(usuarioLogeado.getId()).orElse(usuarioLogeado);

            RecomendacionLibroDTO recLibros = recomendacionesService.obtenerRecomendacionesLibrosParaUsuario(usuarioBD.getId());
            RecomendacionRevistaDTO recRevistas = recomendacionesService.obtenerRecomendacionesRevistasParaUsuario(usuarioBD.getId());

            model.addAttribute("recLibros", recLibros);
            model.addAttribute("recRevistas", recRevistas);

            List<Prestamo> misPrestamos = prestamoRepository.findByUsuario(usuarioBD);
            Libro ultimoLibro = null;
            Revista ultimaRevista = null;

            for (int i = misPrestamos.size() - 1; i >= 0; i--) {
                Material m = misPrestamos.get(i).getMaterial();
                if (m instanceof Libro && ultimoLibro == null) {
                    ultimoLibro = (Libro) m;
                } else if (m instanceof Revista && ultimaRevista == null) {
                    ultimaRevista = (Revista) m;
                }
                if (ultimoLibro != null && ultimaRevista != null) break;
            }

            if (ultimoLibro != null) model.addAttribute("ultimoLibro", ultimoLibro);
            if (ultimaRevista != null) model.addAttribute("ultimaRevista", ultimaRevista);
        }

        return "index";
    }

    //prubas por si deseamos crear un error intencional
    @GetMapping("/test-404")
    public String probar404() {
        throw new ResourceNotFoundException("Este libro mágico no existe en nuestra biblioteca.");
    }

    @GetMapping("/test-regla")
    public String probarReglaNegocio() {
        throw new BusinessRuleException("¡Alerta! Tienes multas pendientes y no puedes pedir más libros.");
    }

}