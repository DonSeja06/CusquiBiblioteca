package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // --- VISTA: MI PERFIL ---
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        // Buscamos al usuario en la BD para tener su saldo actualizado
        Usuario usuarioBD = usuarioRepository.findById(usuarioLogeado.getId()).orElse(usuarioLogeado);
        
        model.addAttribute("usuario", usuarioBD);
        return "perfil"; // Retorna perfil.html
    }

    // --- ACCIÓN: PAGAR MULTA ---
    @PostMapping("/pagar-multa")
    public String procesarPagoMulta(HttpSession session) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        // Traemos al usuario directamente de la BD por seguridad
        Usuario usuario = usuarioRepository.findById(usuarioLogeado.getId()).orElse(null);
        
        if (usuario != null) {
            // ¡MAGIA! Ponemos su deuda en 0.0
            usuario.setMultaAcumulada(0.0f);
            
            // Guardamos el cambio en MySQL
            usuarioRepository.save(usuario);
            
            // Actualizamos la sesión para que el navbar y otras vistas lo sepan
            session.setAttribute("usuarioLogeado", usuario);
        }

        // Lo regresamos a su perfil con un mensaje de éxito
        return "redirect:/perfil?exitoPago=true";
    }
}