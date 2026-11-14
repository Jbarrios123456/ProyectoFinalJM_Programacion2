package com.mycompany.proyectofinal.dao;

import com.mycompany.proyectofinal.config.Conexion;
import com.mycompany.proyectofinal.modelo.Accesorio;
import com.mycompany.proyectofinal.modelo.Alquiler;
import com.mycompany.proyectofinal.modelo.Cliente;
import com.mycompany.proyectofinal.modelo.Comprobante;
import com.mycompany.proyectofinal.modelo.DetalleAccesorio;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.LineaEquipo;
import com.mycompany.proyectofinal.modelo.Liquidacion;
import com.mycompany.proyectofinal.modelo.Pago;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAO {

    private static final String SELECT_BASE = "SELECT a.id_alquiler, a.id_cliente, c.nombre, a.estado, a.hora_estimada, a.hora_entrega, "
            + "a.hora_devolucion, a.deposito, a.recargo, a.reposicion, a.total, a.deposito_devuelto, a.saldo_generado "
            + "FROM alquiler a JOIN cliente c ON c.id_cliente = a.id_cliente";

    private static final int LIMITE_ALQUILERES = 10;

    private final EquipoDAO equipoDAO = new EquipoDAO();

    public List<Alquiler> listar() throws SQLException {
        List<Alquiler> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY a.id_alquiler DESC";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        for (Alquiler alquiler : lista) {
            cargarDetalle(alquiler);
        }
        return lista;
    }

    public Alquiler buscarPorId(int id) throws SQLException {
        Alquiler alquiler = null;
        String sql = SELECT_BASE + " WHERE a.id_alquiler = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    alquiler = mapear(rs);
                }
            }
        }
        if (alquiler != null) {
            cargarDetalle(alquiler);
        }
        return alquiler;
    }

    public boolean clienteTieneAlquilerActivo(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM alquiler WHERE id_cliente = ? AND estado = 'ACTIVO'";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crearReserva(Alquiler alquiler) throws SQLException {
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                int id;
                String sqlAlquiler = "INSERT INTO alquiler (id_cliente, estado, hora_estimada, deposito, recargo, reposicion, total, deposito_devuelto, saldo_generado) "
                        + "VALUES (?, 'RESERVADO', ?, 0, 0, 0, 0, 0, 0)";
                try (PreparedStatement ps = con.prepareStatement(sqlAlquiler, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, alquiler.getCliente().getIdCliente());
                    ps.setObject(2, alquiler.getHoraEstimada());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }
                String sqlReservar = "UPDATE equipo SET estado = 'RESERVADO' WHERE codigo = ? AND estado = 'DISPONIBLE'";
                String sqlEquipo = "INSERT INTO alquiler_equipo (id_alquiler, codigo_equipo, horas) VALUES (?, ?, 0)";
                try (PreparedStatement psReservar = con.prepareStatement(sqlReservar);
                     PreparedStatement psEquipo = con.prepareStatement(sqlEquipo)) {
                    for (Equipo equipo : alquiler.getEquipos()) {
                        psReservar.setInt(1, equipo.getCodigo());
                        if (psReservar.executeUpdate() == 0) {
                            throw new IllegalArgumentException("El equipo " + equipo.getCodigo() + " ya no esta disponible");
                        }
                        psEquipo.setInt(1, id);
                        psEquipo.setInt(2, equipo.getCodigo());
                        psEquipo.executeUpdate();
                    }
                }
                String sqlAccesorio = "INSERT INTO alquiler_accesorio (id_alquiler, codigo_accesorio, cantidad, extraviados) VALUES (?, ?, ?, 0)";
                try (PreparedStatement ps = con.prepareStatement(sqlAccesorio)) {
                    for (DetalleAccesorio detalle : alquiler.getAccesorios()) {
                        ps.setInt(1, id);
                        ps.setInt(2, detalle.getAccesorio().getCodigo());
                        ps.setInt(3, detalle.getCantidad());
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void cancelar(int idAlquiler) throws SQLException {
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                validarEstado(con, idAlquiler, "RESERVADO", "Solo se puede cancelar un alquiler en estado RESERVADO");
                try (PreparedStatement ps = con.prepareStatement("UPDATE alquiler SET estado = 'CANCELADO' WHERE id_alquiler = ?")) {
                    ps.setInt(1, idAlquiler);
                    ps.executeUpdate();
                }
                String sqlLiberar = "UPDATE equipo SET estado = 'DISPONIBLE' WHERE codigo IN (SELECT codigo_equipo FROM alquiler_equipo WHERE id_alquiler = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlLiberar)) {
                    ps.setInt(1, idAlquiler);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void entregar(int idAlquiler, BigDecimal deposito, List<DetalleAccesorio> accesorios) throws SQLException {
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                validarEstado(con, idAlquiler, "RESERVADO", "Solo se puede entregar un alquiler en estado RESERVADO");
                String sqlEquipos = "UPDATE equipo SET estado = 'ALQUILADO' WHERE estado = 'RESERVADO' AND codigo IN (SELECT codigo_equipo FROM alquiler_equipo WHERE id_alquiler = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlEquipos)) {
                    ps.setInt(1, idAlquiler);
                    ps.executeUpdate();
                }
                String sqlDescontar = "UPDATE accesorio SET existencia = existencia - ? WHERE codigo = ? AND existencia >= ?";
                try (PreparedStatement ps = con.prepareStatement(sqlDescontar)) {
                    for (DetalleAccesorio detalle : accesorios) {
                        ps.setInt(1, detalle.getCantidad());
                        ps.setInt(2, detalle.getAccesorio().getCodigo());
                        ps.setInt(3, detalle.getCantidad());
                        if (ps.executeUpdate() == 0) {
                            throw new IllegalArgumentException("No hay existencia suficiente de " + detalle.getAccesorio().getNombre());
                        }
                        registrarMovimiento(con, detalle.getAccesorio().getCodigo(), detalle.getCantidad(), "SALIDA");
                    }
                }
                String sqlAlquiler = "UPDATE alquiler SET estado = 'ACTIVO', hora_entrega = ?, deposito = ? WHERE id_alquiler = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlAlquiler)) {
                    ps.setObject(1, LocalDateTime.now().withSecond(0).withNano(0));
                    ps.setBigDecimal(2, deposito);
                    ps.setInt(3, idAlquiler);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void finalizar(Liquidacion liq, Pago pago) throws SQLException {
        Alquiler alquiler = liq.getAlquiler();
        int id = alquiler.getIdAlquiler();
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                validarEstado(con, id, "ACTIVO", "Solo se puede devolver un alquiler en estado ACTIVO");
                String sqlAlquiler = "UPDATE alquiler SET estado = 'FINALIZADO', hora_devolucion = ?, recargo = ?, reposicion = ?, total = ?, "
                        + "deposito_devuelto = ?, saldo_generado = ? WHERE id_alquiler = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlAlquiler)) {
                    ps.setObject(1, liq.getHoraReal());
                    ps.setBigDecimal(2, liq.getTotalRecargo());
                    ps.setBigDecimal(3, liq.getTotalReposicion());
                    ps.setBigDecimal(4, liq.getTotal());
                    ps.setBigDecimal(5, liq.getDepositoDevuelto());
                    ps.setBigDecimal(6, liq.getSaldoGenerado());
                    ps.setInt(7, id);
                    ps.executeUpdate();
                }
                String sqlDetalle = "UPDATE alquiler_equipo SET estado_devolucion = ?, horas = ? WHERE id_alquiler = ? AND codigo_equipo = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (LineaEquipo linea : liq.getLineas()) {
                        ps.setString(1, linea.getEstadoDevolucion());
                        ps.setInt(2, linea.getHoras());
                        ps.setInt(3, id);
                        ps.setInt(4, linea.getEquipo().getCodigo());
                        ps.executeUpdate();
                        actualizarEquipoDevuelto(con, linea, id);
                    }
                }
                for (DetalleAccesorio detalle : alquiler.getAccesorios()) {
                    int codigo = detalle.getAccesorio().getCodigo();
                    try (PreparedStatement ps = con.prepareStatement("UPDATE alquiler_accesorio SET extraviados = ? WHERE id_alquiler = ? AND codigo_accesorio = ?")) {
                        ps.setInt(1, detalle.getExtraviados());
                        ps.setInt(2, id);
                        ps.setInt(3, codigo);
                        ps.executeUpdate();
                    }
                    if (detalle.getDevueltos() > 0) {
                        try (PreparedStatement ps = con.prepareStatement("UPDATE accesorio SET existencia = existencia + ? WHERE codigo = ?")) {
                            ps.setInt(1, detalle.getDevueltos());
                            ps.setInt(2, codigo);
                            ps.executeUpdate();
                        }
                        registrarMovimiento(con, codigo, detalle.getDevueltos(), "ENTRADA");
                    }
                }
                if (liq.getSaldoGenerado().compareTo(BigDecimal.ZERO) > 0) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE cliente SET saldo_pendiente = saldo_pendiente + ? WHERE id_cliente = ?")) {
                        ps.setBigDecimal(1, liq.getSaldoGenerado());
                        ps.setInt(2, alquiler.getCliente().getIdCliente());
                        ps.executeUpdate();
                    }
                }
                insertarPago(con, id, pago, "APROBADO");
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void registrarPagoRechazado(int idAlquiler, Pago pago) throws SQLException {
        try (Connection con = Conexion.obtener()) {
            insertarPago(con, idAlquiler, pago, "RECHAZADO");
        }
    }

    public Comprobante comprobante(int idAlquiler) throws SQLException {
        Alquiler alquiler = buscarPorId(idAlquiler);
        if (alquiler == null || !"FINALIZADO".equals(alquiler.getEstado())) {
            return null;
        }
        Comprobante comprobante = new Comprobante(alquiler);
        String sqlLineas = "SELECT codigo_equipo, horas, estado_devolucion FROM alquiler_equipo WHERE id_alquiler = ? ORDER BY codigo_equipo";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlLineas)) {
            ps.setInt(1, idAlquiler);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int codigo = rs.getInt("codigo_equipo");
                    for (Equipo equipo : alquiler.getEquipos()) {
                        if (equipo.getCodigo() == codigo) {
                            boolean danado = "DANADO".equals(rs.getString("estado_devolucion"));
                            comprobante.getLineas().add(new LineaEquipo(equipo, rs.getInt("horas"), 0, danado));
                        }
                    }
                }
            }
        }
        String sqlPago = "SELECT metodo, monto, cambio, referencia, fecha FROM pago WHERE id_alquiler = ? AND resultado = 'APROBADO' ORDER BY id_pago DESC LIMIT 1";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlPago)) {
            ps.setInt(1, idAlquiler);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    comprobante.setMetodo(rs.getString("metodo"));
                    comprobante.setMontoPagado(rs.getBigDecimal("monto"));
                    comprobante.setCambio(rs.getBigDecimal("cambio"));
                    comprobante.setReferencia(rs.getString("referencia"));
                    comprobante.setFechaPago(rs.getObject("fecha", LocalDateTime.class));
                }
            }
        }
        return comprobante;
    }

    private void actualizarEquipoDevuelto(Connection con, LineaEquipo linea, int idAlquiler) throws SQLException {
        int codigo = linea.getEquipo().getCodigo();
        int contador = linea.getEquipo().getAlquileresDesdeMantenimiento() + 1;
        String sql = "UPDATE equipo SET estado = ?, alquileres_desde_mantenimiento = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (linea.isDanado()) {
                ps.setString(1, "EN_MANTENIMIENTO");
                ps.setInt(2, 0);
            } else if (contador >= LIMITE_ALQUILERES) {
                ps.setString(1, "EN_MANTENIMIENTO");
                ps.setInt(2, 0);
            } else {
                ps.setString(1, "DISPONIBLE");
                ps.setInt(2, contador);
            }
            ps.setInt(3, codigo);
            ps.executeUpdate();
        }
        if (linea.isDanado()) {
            insertarMantenimiento(con, codigo, "Equipo devuelto con danos en alquiler #" + idAlquiler);
        } else if (contador >= LIMITE_ALQUILERES) {
            insertarMantenimiento(con, codigo, "Mantenimiento preventivo por " + LIMITE_ALQUILERES + " alquileres finalizados");
        }
    }

    private void insertarMantenimiento(Connection con, int codigoEquipo, String motivo) throws SQLException {
        String sql = "INSERT INTO mantenimiento (codigo_equipo, fecha, motivo, costo, estado) VALUES (?, ?, ?, 0, 'ABIERTO')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoEquipo);
            ps.setObject(2, LocalDate.now());
            ps.setString(3, motivo);
            ps.executeUpdate();
        }
    }

    private void insertarPago(Connection con, int idAlquiler, Pago pago, String resultado) throws SQLException {
        String sql = "INSERT INTO pago (id_alquiler, metodo, monto, cambio, referencia, resultado, fecha) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlquiler);
            ps.setString(2, pago.getMetodo());
            ps.setBigDecimal(3, pago.getMonto());
            ps.setBigDecimal(4, pago.getCambio());
            ps.setString(5, pago.getReferencia());
            ps.setString(6, resultado);
            ps.setObject(7, LocalDateTime.now().withNano(0));
            ps.executeUpdate();
        }
    }

    private void registrarMovimiento(Connection con, int codigoAccesorio, int cantidad, String tipo) throws SQLException {
        String sql = "INSERT INTO movimiento_accesorio (codigo_accesorio, fecha, cantidad, tipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoAccesorio);
            ps.setObject(2, LocalDateTime.now().withNano(0));
            ps.setInt(3, cantidad);
            ps.setString(4, tipo);
            ps.executeUpdate();
        }
    }

    private void validarEstado(Connection con, int idAlquiler, String esperado, String mensaje) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT estado FROM alquiler WHERE id_alquiler = ? FOR UPDATE")) {
            ps.setInt(1, idAlquiler);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("El alquiler no existe");
                }
                if (!esperado.equals(rs.getString("estado"))) {
                    throw new IllegalArgumentException(mensaje);
                }
            }
        }
    }

    private Alquiler mapear(ResultSet rs) throws SQLException {
        Alquiler alquiler = new Alquiler();
        alquiler.setIdAlquiler(rs.getInt("id_alquiler"));
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre"));
        alquiler.setCliente(cliente);
        alquiler.setEstado(rs.getString("estado"));
        alquiler.setHoraEstimada(rs.getObject("hora_estimada", LocalDateTime.class));
        alquiler.setHoraEntrega(rs.getObject("hora_entrega", LocalDateTime.class));
        alquiler.setHoraDevolucion(rs.getObject("hora_devolucion", LocalDateTime.class));
        alquiler.setDeposito(cero(rs.getBigDecimal("deposito")));
        alquiler.setRecargo(cero(rs.getBigDecimal("recargo")));
        alquiler.setReposicion(cero(rs.getBigDecimal("reposicion")));
        alquiler.setTotal(cero(rs.getBigDecimal("total")));
        alquiler.setDepositoDevuelto(cero(rs.getBigDecimal("deposito_devuelto")));
        alquiler.setSaldoGenerado(cero(rs.getBigDecimal("saldo_generado")));
        return alquiler;
    }

    private BigDecimal cero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private void cargarDetalle(Alquiler alquiler) throws SQLException {
        List<Integer> codigos = new ArrayList<>();
        String sqlEquipos = "SELECT codigo_equipo FROM alquiler_equipo WHERE id_alquiler = ? ORDER BY codigo_equipo";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlEquipos)) {
            ps.setInt(1, alquiler.getIdAlquiler());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codigos.add(rs.getInt("codigo_equipo"));
                }
            }
        }
        for (int codigo : codigos) {
            Equipo equipo = equipoDAO.buscarPorId(codigo);
            if (equipo != null) {
                alquiler.getEquipos().add(equipo);
            }
        }
        String sqlAccesorios = "SELECT aa.cantidad, aa.extraviados, ac.codigo, ac.nombre, ac.tarifa, ac.precio_reposicion, ac.existencia "
                + "FROM alquiler_accesorio aa JOIN accesorio ac ON ac.codigo = aa.codigo_accesorio WHERE aa.id_alquiler = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlAccesorios)) {
            ps.setInt(1, alquiler.getIdAlquiler());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Accesorio accesorio = new Accesorio(
                            rs.getInt("codigo"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("tarifa"),
                            rs.getBigDecimal("precio_reposicion"),
                            rs.getInt("existencia"));
                    DetalleAccesorio detalle = new DetalleAccesorio(accesorio, rs.getInt("cantidad"));
                    detalle.setExtraviados(rs.getInt("extraviados"));
                    alquiler.getAccesorios().add(detalle);
                }
            }
        }
    }
}
