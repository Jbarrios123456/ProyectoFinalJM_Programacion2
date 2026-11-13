package com.mycompany.proyectofinal.controlador;

import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.Scooter;
import com.mycompany.proyectofinal.servicio.EquipoServicio;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipos")
public class EquipoControlador {

    private final EquipoServicio servicio = new EquipoServicio();

    @GetMapping
    public String listar(Model model) {
        cargarVista(model, null, false);
        return "equipos";
    }

    @GetMapping("/editar/{codigo}")
    public String editar(@PathVariable int codigo, Model model) {
        try {
            Equipo equipo = servicio.buscar(codigo);
            if (equipo == null) {
                cargarVista(model, null, false);
                model.addAttribute("error", "El equipo no existe");
            } else {
                cargarVista(model, equipo, true);
            }
        } catch (SQLException e) {
            cargarVista(model, null, false);
            model.addAttribute("error", "Error de base de datos: " + e.getMessage());
        }
        return "equipos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String tipo, @RequestParam(required = false) Integer codigo,
            @RequestParam String modelo, @RequestParam(required = false) BigDecimal tarifaHora,
            @RequestParam(required = false) BigDecimal precioReposicion,
            @RequestParam(required = false) Integer bateria, RedirectAttributes ra) {
        try {
            servicio.registrar(tipo, codigo, modelo, tarifaHora, precioReposicion, bateria);
            ra.addFlashAttribute("mensaje", "Equipo registrado correctamente");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String tipo, @RequestParam(required = false) Integer codigo,
            @RequestParam String modelo, @RequestParam(required = false) BigDecimal tarifaHora,
            @RequestParam(required = false) BigDecimal precioReposicion,
            @RequestParam(required = false) Integer bateria, RedirectAttributes ra) {
        try {
            servicio.actualizar(tipo, codigo, modelo, tarifaHora, precioReposicion, bateria);
            ra.addFlashAttribute("mensaje", "Equipo actualizado correctamente");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/baja/{codigo}")
    public String darDeBaja(@PathVariable int codigo, RedirectAttributes ra) {
        try {
            servicio.darDeBaja(codigo);
            ra.addFlashAttribute("mensaje", "Equipo dado de baja");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    private void cargarVista(Model model, Equipo equipo, boolean editando) {
        try {
            model.addAttribute("equipos", servicio.listar());
        } catch (SQLException e) {
            model.addAttribute("error", "Error de base de datos: " + e.getMessage());
            model.addAttribute("equipos", new ArrayList<Equipo>());
        }
        model.addAttribute("equipo", equipo);
        model.addAttribute("bateria", equipo instanceof Scooter s ? s.getBateria() : null);
        model.addAttribute("editando", editando);
    }
}
