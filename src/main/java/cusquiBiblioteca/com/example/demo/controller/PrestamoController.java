package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.exception.BusinessRuleException;
import cusquiBiblioteca.com.example.demo.exception.ResourceNotFoundException;
import cusquiBiblioteca.com.example.demo.model.Prestamo;
import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.PrestamoRepository;
import cusquiBiblioteca.com.example.demo.service.PrestamoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoRepository prestamoRepository, PrestamoService prestamoService) {
        this.prestamoRepository = prestamoRepository;
        this.prestamoService = prestamoService;
    }

    @GetMapping("/mis-prestamos")
    public String verMisPrestamos(HttpSession session, Model model) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        List<Prestamo> misPrestamos = prestamoRepository.findByUsuario(usuarioLogeado);
        model.addAttribute("prestamos", misPrestamos);

        return "mis-prestamos";
    }

    @PostMapping("/solicitar-prestamo")
    public String solicitarPrestamo(@RequestParam("materialId") Long materialId, HttpSession session) {

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        prestamoService.solicitarPrestamo(usuarioLogeado, materialId);

        return "redirect:/mis-prestamos";
    }


    @PostMapping("/devolver-prestamo")
    public String devolverPrestamo(@RequestParam("prestamoId") Long prestamoId, HttpSession session) {

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        boolean conRetraso = prestamoService.devolverPrestamo(usuarioLogeado, prestamoId);

        if (conRetraso) {
            return "redirect:/mis-prestamos?exito=devuelto&retraso=true";
        }

        return "redirect:/mis-prestamos?exito=devuelto";
    }
}