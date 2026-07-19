package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;

    public RegistroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Muestra el formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // Enviamos un objeto Usuario vacío para que Thymeleaf lo llene
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Guarda el nuevo usuario en la base de datos
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        // Aquí se guarda directamente en MySQL
        // (Nota: En el futuro, aquí deberías encriptar la contraseña)
        usuarioRepository.save(usuario);
        
        // Redirigimos al login con un parámetro de éxito
        return "redirect:/login?exitoRegistro=true";
    }
}