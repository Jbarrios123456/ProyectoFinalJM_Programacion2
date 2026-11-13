package com.mycompany.proyectofinal.controlador;

import com.mycompany.proyectofinal.modelo.Cliente;
import com.mycompany.proyectofinal.servicio.ClienteServicio;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteServicio servicio = new ClienteServicio();

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("clientes", servicio.listar());
        } catch (SQLException e) {
            model.addAttribute("error", "Error de base de datos: " + e.getMessage());
            model.addAttribute("clientes", new ArrayList<Cliente>());
        }
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("editando", false);
        return "clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        try {
            model.addAttribute("clientes", servicio.listar());
            Cliente c = servicio.buscar(id);
            if (c == null) {
                model.addAttribute("error", "El cliente no existe");
                c = new Cliente();
                model.addAttribute("editando", false);
            } else {
                model.addAttribute("editando", true);
            }
            model.addAttribute("cliente", c);
        } catch (SQLException e) {
            model.addAttribute("error", "Error de base de datos: " + e.getMessage());
            model.addAttribute("clientes", new ArrayList<Cliente>());
            model.addAttribute("cliente", new Cliente());
            model.addAttribute("editando", false);
        }
        return "clientes";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes ra) {
        try {
            servicio.registrar(cliente);
            ra.addFlashAttribute("mensaje", "Cliente registrado correctamente");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente, RedirectAttributes ra) {
        try {
            servicio.actualizar(cliente);
            ra.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable int id, RedirectAttributes ra) {
        try {
            servicio.desactivar(id);
            ra.addFlashAttribute("mensaje", "Cliente desactivado");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable int id, RedirectAttributes ra) {
        try {
            servicio.activar(id);
            ra.addFlashAttribute("mensaje", "Cliente activado");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
