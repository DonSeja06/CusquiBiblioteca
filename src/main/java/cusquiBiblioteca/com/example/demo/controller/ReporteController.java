package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String verReportes(HttpSession session, Model model) {
        // En un sistema real esto lo verificaría Spring Security
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }
        
        // Podríamos agregar rol admin y verificar aquí, por ahora permitimos ver los reportes
        model.addAttribute("materialesMasPrestados", reporteService.obtenerMaterialesMasPrestados());
        model.addAttribute("usuariosMasPrestamos", reporteService.obtenerUsuariosConMasPrestamos());
        model.addAttribute("materialesNuncaPrestados", reporteService.obtenerMaterialesNuncaPrestados());
        model.addAttribute("montoTotalMultas", reporteService.obtenerMontoTotalMultas());
        model.addAttribute("categoriasPopulares", reporteService.obtenerCategoriasMasPopulares());

        return "reportes";
    }
}
