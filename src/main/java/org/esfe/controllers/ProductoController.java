package org.esfe.controllers;

import org.esfe.models.ProductoKDSB;
import org.esfe.servicios.Interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1; // si no está seteado se asigna 0
        int pageSize = size.orElse(5); // tamaño de la página, se asigna 5
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<ProductoKDSB> productos = productoService.buscarTodosPaginados(pageable);
        model.addAttribute("productos", productos);

        int totalPages = productos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "producto/index";
    }

    @GetMapping("/create")
    public String create(ProductoKDSB productoKDSB) {
        return "productoKDSB/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("productoKDSB") ProductoKDSB productoKDSB, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(productoKDSB);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "productoKDSB/create";
        }

        productoService.crearOEditar(productoKDSB);
        attributes.addFlashAttribute("msg", "ProductoKDSB creado correctamente");
        return "redirect:/productos";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        ProductoKDSB productoKDSB = productoService.buscarPorId(id).orElse(null);
        if (productoKDSB != null) {
            String formattedDate = productoKDSB.getFechaVencimiento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            model.addAttribute("fechaVencimiento", formattedDate);
        }
        model.addAttribute("productoKDSB", productoKDSB);
        return "productoKDSB/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        ProductoKDSB productoKDSB = productoService.buscarPorId(id).get();
        model.addAttribute("productoKDSB", productoKDSB);
        return "productoKDSB/edit";
    }

    @GetMapping("/remove/{id}")
    public String delete(@PathVariable("id") Integer id, Model model) {
        ProductoKDSB productoKDSB = productoService.buscarPorId(id).orElse(null);
        if (productoKDSB != null) {
            String formattedDate = productoKDSB.getFechaVencimiento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            model.addAttribute("fechaVencimiento", formattedDate);
        }
        model.addAttribute("productoKDSB", productoKDSB);
        return "productoKDSB/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
        productoService.eliminarPorId(id);
        attributes.addFlashAttribute("msg", "ProductoKDSB eliminado correctamente");
        return "redirect:/productos";
    }
}