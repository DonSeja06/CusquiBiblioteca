package cusquiBiblioteca.com.example.demo.service;

import cusquiBiblioteca.com.example.demo.model.Revista;
import cusquiBiblioteca.com.example.demo.repository.RevistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RevistaService {

    private final RevistaRepository revistaRepository;

    public RevistaService(RevistaRepository revistaRepository) {
        this.revistaRepository = revistaRepository;
    }

    public void buscarRevistasPorVolumen(int volumen) {
        System.out.println("--- Revistas del Volumen: " + volumen + " ---");
        revistaRepository.findAll().stream()
                .filter(revista -> revista.getVolumen() == volumen)
                .forEach(revista -> System.out.println(
                        "- " + revista.getNombre() + " (Edición: " + revista.getEdicion() + ")"
                ));
    }

    public List<String> obtenerNombresRevistasPrestadas() {
        return revistaRepository.findAll().stream()
                .filter(revista -> !revista.isDisponible()) 
                .map(Revista::getNombre)
                .collect(Collectors.toList());
    }
    

    public Integer calcularSumaDeEdiciones() {
        return revistaRepository.findAll().stream()
                .map(Revista::getEdicion)
                .reduce(0, Integer::sum);
    }
}