package cusquiBiblioteca.com.example.demo.controller;

import cusquiBiblioteca.com.example.demo.model.Material;
import cusquiBiblioteca.com.example.demo.repository.MaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogoController {

    private final MaterialRepository materialRepository;

    public CatalogoController(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @GetMapping("/catalogo")
    public String mostrarCatalogo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoria", required = false) String categoria,
            Model model) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Material> paginaMateriales;

        // LÓGICA DE FILTROS Y BÚSQUEDA
        if (search != null && !search.trim().isEmpty()) {
            paginaMateriales = materialRepository.buscarPorTermino(search.trim(), pageable);
        } else if ("libro".equalsIgnoreCase(categoria)) {
            paginaMateriales = materialRepository.findAllLibros(pageable);
        } else if ("revista".equalsIgnoreCase(categoria)) {
            paginaMateriales = materialRepository.findAllRevistas(pageable);
        } else {
            paginaMateriales = materialRepository.findAll(pageable);
        }

        model.addAttribute("materiales", paginaMateriales.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaMateriales.getTotalPages());
        model.addAttribute("tieneAnterior", paginaMateriales.hasPrevious());
        model.addAttribute("tieneSiguiente", paginaMateriales.hasNext());

        model.addAttribute("search", search);
        model.addAttribute("categoriaSeleccionada", categoria);

        return "catalogo";
    }
}