package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.model.Usuario;
import cusquiBiblioteca.com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Float calcularTotalMultasPorCobrar() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getMultaAcumulada() > 0)
                .map(Usuario::getMultaAcumulada)
                .reduce(0.0f, Float::sum);
    }
    

    public void listarUsuariosBloqueados() {
        System.out.println("--- Usuarios con multas críticas ---");
        usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getMultaAcumulada() >= 50.0f)
                .forEach(usuario -> System.out.println(
                        usuario.getNombre() + " (DNI: " + usuario.getDNI() + ") - Deuda: S/" + usuario.getMultaAcumulada()
                ));
    }
}