package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 1. MUESTRA LA PANTALLA
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos. Inténtalo de nuevo.");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente.");
        }
        return "login";
    }

    // 2. PROCESA LOS DATOS CUANDO LE DAS CLICK A "INICIAR SESIÓN"
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String correo, 
                                @RequestParam("password") String contrasena, 
                                HttpSession session) {
                                    
        // Buscamos si existe alguien con ese correo
        Optional<Usuario> usuarioOpcional = usuarioRepository.findByCorreo(correo);

        // Si el usuario existe y la contraseña es exactamente igual
        if (usuarioOpcional.isPresent() && usuarioOpcional.get().getContrasena().equals(contrasena)) {
            
            // ¡Éxito! Guardamos los datos del usuario en la "Sesión"
            Usuario usuarioLogeado = usuarioOpcional.get();
            session.setAttribute("usuarioLogeado", usuarioLogeado);
            
            // Lo enviamos a la página principal
            return "redirect:/"; 
        }

        // Si falla, lo regresamos al login marcando un error
        return "redirect:/login?error=true";
    }

    // 3. CERRAR SESIÓN
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Borramos la memoria
        return "redirect:/login?logout=true";
    }
}