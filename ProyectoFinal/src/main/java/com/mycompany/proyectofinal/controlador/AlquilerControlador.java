package com.mycompany.proyectofinal.controlador;

import com.mycompany.proyectofinal.modelo.Accesorio;
import com.mycompany.proyectofinal.modelo.Alquiler;
import com.mycompany.proyectofinal.modelo.Cliente;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.Liquidacion;
import com.mycompany.proyectofinal.modelo.Pago;
import com.mycompany.proyectofinal.modelo.PagoEfectivo;
import com.mycompany.proyectofinal.modelo.PagoTarjeta;
import com.mycompany.proyectofinal.servicio.AlquilerServicio;
import com.mycompany.proyectofinal.servicio.PagoRechazadoException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alquileres")
public class AlquilerControlador {

    private final AlquilerServicio servicio = new AlquilerServicio();

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("alquileres", servicio.listar());
            model.addAttribute("clientes", servicio.clientes());
            model.addAttribute("equipos", servicio.equiposDisponibles());
            model.addAttribute("accesorios", servicio.accesorios());
        } catch (SQLException e) {
            model.addAttribute("error", "Error de base de datos: " + e.getMessage());
            model.addAttribute("alquileres", new ArrayList<Alquiler>());
            model.addAttribute("clientes", new ArrayList<Cliente>());
            model.addAttribute("equipos", new ArrayList<Equipo>());
            model.addAttribute("accesorios", new ArrayList<Accesorio>());
        }
        return "alquileres";
    }

    @PostMapping("/reservar")
    public String reservar(@RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) List<Integer> equipos,
            @RequestParam(required = false) String horaEstimada,
            @RequestParam Map<String, String> parametros, RedirectAttributes ra) {
        try {
            Map<Integer, Integer> cantidades = leerNumeros(parametros, "cant_");
            LocalDateTime hora = (horaEstimada == null || horaEstimada.isBlank()) ? null : LocalDateTime.parse(horaEstimada);
            int id = servicio.reservar(idCliente, equipos, cantidades, hora);
            ra.addFlashAttribute("mensaje", "Alquiler #" + id + " reservado correctamente");
        } catch (IllegalArgumentException | DateTimeParseException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/alquileres";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable int id, RedirectAttributes ra) {
        try {
            servicio.cancelar(id);
            ra.addFlashAttribute("mensaje", "Alquiler #" + id + " cancelado, equipos liberados");
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/alquileres";
    }

    @PostMapping("/entregar/{id}")
    public String entregar(@PathVariable int id, RedirectAttributes ra) {
        try {
            BigDecimal deposito = servicio.entregar(id);
            ra.addFlashAttribute("mensaje", "Alquiler #" + id + " entregado. Deposito cobrado: Q" + deposito);
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/alquileres";
    }

    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable int id, Model model, RedirectAttributes ra) {
        try {
            Alquiler alquiler = servicio.buscar(id);
            if (!"ACTIVO".equals(alquiler.getEstado())) {
                ra.addFlashAttribute("error", "Solo se puede devolver un alquiler en estado ACTIVO");
                return "redirect:/alquileres";
            }
            model.addAttribute("alquiler", alquiler);
            model.addAttribute("ahora", LocalDateTime.now().withSecond(0).withNano(0).toString());
            return "devolucion";
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres";
        }
    }

    @PostMapping("/liquidar/{id}")
    public String liquidar(@PathVariable int id, @RequestParam(required = false) String horaReal,
            @RequestParam Map<String, String> parametros, Model model, RedirectAttributes ra) {
        try {
            LocalDateTime hora = (horaReal == null || horaReal.isBlank()) ? null : LocalDateTime.parse(horaReal);
            Liquidacion liq = servicio.liquidar(id, leerEstados(parametros), leerNumeros(parametros, "ext_"), hora);
            model.addAttribute("liq", liq);
            return "liquidacion";
        } catch (IllegalArgumentException | DateTimeParseException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres/devolver/" + id;
        }
    }

    @PostMapping("/pagar/{id}")
    public String pagar(@PathVariable int id, @RequestParam String horaReal, @RequestParam String metodo,
            @RequestParam(required = false) BigDecimal montoRecibido,
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam Map<String, String> parametros, Model model, RedirectAttributes ra) {
        Liquidacion liq = null;
        try {
            liq = servicio.liquidar(id, leerEstados(parametros), leerNumeros(parametros, "ext_"), LocalDateTime.parse(horaReal));
            Pago pago = "TARJETA".equals(metodo)
                    ? new PagoTarjeta(liq.getMontoAPagar(), numeroTarjeta)
                    : new PagoEfectivo(liq.getMontoAPagar(), montoRecibido);
            servicio.pagar(liq, pago);
            String detalle = "EFECTIVO".equals(pago.getMetodo()) ? " Cambio: Q" + pago.getCambio() : " Referencia: " + pago.getReferencia();
            ra.addFlashAttribute("mensaje", "Pago aprobado, alquiler #" + id + " finalizado." + detalle);
            return "redirect:/alquileres/comprobante/" + id;
        } catch (IllegalArgumentException | DateTimeParseException | SQLException | PagoRechazadoException e) {
            if (liq == null) {
                ra.addFlashAttribute("error", e.getMessage());
                return "redirect:/alquileres";
            }
            model.addAttribute("liq", liq);
            model.addAttribute("error", e.getMessage());
            return "liquidacion";
        }
    }

    @GetMapping("/comprobante/{id}")
    public String comprobante(@PathVariable int id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("c", servicio.comprobante(id));
            return "comprobante";
        } catch (IllegalArgumentException | SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres";
        }
    }

    private Map<Integer, String> leerEstados(Map<String, String> parametros) {
        Map<Integer, String> estados = new LinkedHashMap<>();
        for (Map.Entry<String, String> parametro : parametros.entrySet()) {
            if (parametro.getKey().startsWith("estado_")) {
                estados.put(Integer.parseInt(parametro.getKey().substring(7)), parametro.getValue());
            }
        }
        return estados;
    }

    private Map<Integer, Integer> leerNumeros(Map<String, String> parametros, String prefijo) {
        Map<Integer, Integer> valores = new LinkedHashMap<>();
        for (Map.Entry<String, String> parametro : parametros.entrySet()) {
            String valor = parametro.getValue();
            if (parametro.getKey().startsWith(prefijo) && valor != null && !valor.isBlank()) {
                try {
                    valores.put(Integer.parseInt(parametro.getKey().substring(prefijo.length())), Integer.parseInt(valor.trim()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cantidad invalida: " + valor);
                }
            }
        }
        return valores;
    }
}
