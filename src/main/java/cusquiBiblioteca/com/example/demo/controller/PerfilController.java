package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.exception.ResourceNotFoundException;
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

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        Usuario usuarioBD = usuarioRepository.findById(usuarioLogeado.getId()).orElse(usuarioLogeado);

        model.addAttribute("usuario", usuarioBD);
        return "perfil";
    }

    @PostMapping("/pagar-multa")
    public String procesarPagoMulta(HttpSession session) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findById(usuarioLogeado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe en el sistema."));

        usuario.setMultaAcumulada(0.0f);
        usuarioRepository.save(usuario);

        session.setAttribute("usuarioLogeado", usuario);
        

        return "redirect:/perfil?exitoPago=true";
    }
}