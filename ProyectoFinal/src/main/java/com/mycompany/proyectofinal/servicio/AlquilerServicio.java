package com.mycompany.proyectofinal.servicio;

import com.mycompany.proyectofinal.dao.AccesorioDAO;
import com.mycompany.proyectofinal.dao.AlquilerDAO;
import com.mycompany.proyectofinal.dao.clienteDAO;
import com.mycompany.proyectofinal.dao.EquipoDAO;
import com.mycompany.proyectofinal.modelo.Accesorio;
import com.mycompany.proyectofinal.modelo.Alquiler;
import com.mycompany.proyectofinal.modelo.Cliente;
import com.mycompany.proyectofinal.modelo.Comprobante;
import com.mycompany.proyectofinal.modelo.DetalleAccesorio;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.Liquidacion;
import com.mycompany.proyectofinal.modelo.Pago;
import com.mycompany.proyectofinal.modelo.Scooter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlquilerServicio {

    private final AlquilerDAO alquilerDAO = new AlquilerDAO();
    private final clienteDAO clienteDAO = new clienteDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final AccesorioDAO accesorioDAO = new AccesorioDAO();
    private final CalculadoraCobro calculadora = new CalculadoraCobro();

    public List<Alquiler> listar() throws SQLException {
        return alquilerDAO.listar();
    }

    public Alquiler buscar(int id) throws SQLException {
        Alquiler alquiler = alquilerDAO.buscarPorId(id);
        if (alquiler == null) {
            throw new IllegalArgumentException("El alquiler no existe");
        }
        return alquiler;
    }

    public List<Cliente> clientes() throws SQLException {
        return clienteDAO.listar();
    }

    public List<Equipo> equiposDisponibles() throws SQLException {
        List<Equipo> disponibles = new ArrayList<>();
        for (Equipo equipo : equipoDAO.listar()) {
            if ("DISPONIBLE".equals(equipo.getEstado())) {
                disponibles.add(equipo);
            }
        }
        return disponibles;
    }

    public List<Accesorio> accesorios() throws SQLException {
        return accesorioDAO.listar();
    }

    public int reservar(Integer idCliente, List<Integer> codigosEquipos, Map<Integer, Integer> cantidades, LocalDateTime horaEstimada) throws SQLException {
        Cliente cliente = validarCliente(idCliente);
        if (codigosEquipos == null || codigosEquipos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un equipo");
        }
        if (horaEstimada == null || !horaEstimada.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La hora estimada de devolucion debe ser posterior a la hora actual");
        }
        Alquiler alquiler = new Alquiler();
        alquiler.setCliente(cliente);
        alquiler.setHoraEstimada(horaEstimada);
        List<String> problemas = new ArrayList<>();
        for (Integer codigo : codigosEquipos) {
            Equipo equipo = equipoDAO.buscarPorId(codigo);
            if (equipo == null) {
                problemas.add("El equipo " + codigo + " no existe");
            } else if (!"DISPONIBLE".equals(equipo.getEstado())) {
                problemas.add("El equipo " + codigo + " no esta disponible (" + equipo.getEstado() + ")");
            } else if (!equipo.puedeAlquilarse()) {
                problemas.add("El scooter " + codigo + " tiene bateria menor al " + Scooter.BATERIA_MINIMA + "%");
            } else {
                alquiler.getEquipos().add(equipo);
            }
        }
        if (cantidades != null) {
            for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
                if (entrada.getValue() == null || entrada.getValue() <= 0) {
                    continue;
                }
                Accesorio accesorio = accesorioDAO.buscarPorId(entrada.getKey());
                if (accesorio == null) {
                    problemas.add("El accesorio " + entrada.getKey() + " no existe");
                } else if (accesorio.getExistencia() < entrada.getValue()) {
                    problemas.add("No hay suficiente " + accesorio.getNombre() + " (disponibles: " + accesorio.getExistencia() + ")");
                } else {
                    alquiler.getAccesorios().add(new DetalleAccesorio(accesorio, entrada.getValue()));
                }
            }
        }
        if (!problemas.isEmpty()) {
            throw new IllegalArgumentException("Reserva rechazada: " + String.join("; ", problemas));
        }
        return alquilerDAO.crearReserva(alquiler);
    }

    public void cancelar(int idAlquiler) throws SQLException {
        alquilerDAO.cancelar(idAlquiler);
    }

    public BigDecimal entregar(int idAlquiler) throws SQLException {
        Alquiler alquiler = buscar(idAlquiler);
        if (!"RESERVADO".equals(alquiler.getEstado())) {
            throw new IllegalArgumentException("Solo se puede entregar un alquiler en estado RESERVADO");
        }
        BigDecimal deposito = BigDecimal.ZERO;
        for (Equipo equipo : alquiler.getEquipos()) {
            deposito = deposito.add(equipo.getDeposito());
        }
        alquilerDAO.entregar(idAlquiler, deposito, alquiler.getAccesorios());
        return deposito;
    }

    public Liquidacion liquidar(int idAlquiler, Map<Integer, String> estados, Map<Integer, Integer> extraviados, LocalDateTime horaReal) throws SQLException {
        Alquiler alquiler = buscar(idAlquiler);
        if (!"ACTIVO".equals(alquiler.getEstado())) {
            throw new IllegalArgumentException("Solo se puede devolver un alquiler en estado ACTIVO");
        }
        if (horaReal == null) {
            throw new IllegalArgumentException("Debe indicar la hora real de devolucion");
        }
        if (horaReal.isBefore(alquiler.getHoraEntrega())) {
            throw new IllegalArgumentException("La hora de devolucion no puede ser anterior a la entrega (" + alquiler.getHoraEntregaTexto() + ")");
        }
        Map<Integer, String> estadosValidos = new LinkedHashMap<>();
        for (Equipo equipo : alquiler.getEquipos()) {
            String estado = estados.getOrDefault(equipo.getCodigo(), "BUENO");
            if (!"BUENO".equals(estado) && !"DANADO".equals(estado)) {
                throw new IllegalArgumentException("Estado de devolucion invalido para el equipo " + equipo.getCodigo());
            }
            estadosValidos.put(equipo.getCodigo(), estado);
        }
        Map<Integer, Integer> extraviadosValidos = new LinkedHashMap<>();
        for (DetalleAccesorio detalle : alquiler.getAccesorios()) {
            int codigo = detalle.getAccesorio().getCodigo();
            int cantidad = extraviados.getOrDefault(codigo, 0);
            if (cantidad < 0 || cantidad > detalle.getCantidad()) {
                throw new IllegalArgumentException("Los extraviados de " + detalle.getAccesorio().getNombre() + " deben estar entre 0 y " + detalle.getCantidad());
            }
            extraviadosValidos.put(codigo, cantidad);
        }
        return calculadora.calcular(alquiler, estadosValidos, extraviadosValidos, horaReal);
    }

    public void pagar(Liquidacion liquidacion, Pago pago) throws SQLException, PagoRechazadoException {
        pago.procesar();
        if (!pago.isAprobado()) {
            alquilerDAO.registrarPagoRechazado(liquidacion.getAlquiler().getIdAlquiler(), pago);
            throw new PagoRechazadoException("Pago con tarjeta rechazado (referencia " + pago.getReferencia()
                    + "). El alquiler sigue ACTIVO, puede reintentar el cobro.");
        }
        alquilerDAO.finalizar(liquidacion, pago);
    }

    public Comprobante comprobante(int idAlquiler) throws SQLException {
        Comprobante comprobante = alquilerDAO.comprobante(idAlquiler);
        if (comprobante == null) {
            throw new IllegalArgumentException("El alquiler no esta finalizado, no tiene comprobante");
        }
        return comprobante;
    }

    private Cliente validarCliente(Integer idCliente) throws SQLException {
        Cliente cliente = idCliente == null ? null : clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente valido");
        }
        if (!cliente.isActivo()) {
            throw new IllegalArgumentException("El cliente esta desactivado y no puede iniciar un alquiler");
        }
        if (cliente.getSaldoPendiente() != null && cliente.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("El cliente tiene un saldo pendiente de Q" + cliente.getSaldoPendiente());
        }
        if (alquilerDAO.clienteTieneAlquilerActivo(cliente.getIdCliente())) {
            throw new IllegalArgumentException("El cliente ya tiene un alquiler activo");
        }
        return cliente;
    }
}
