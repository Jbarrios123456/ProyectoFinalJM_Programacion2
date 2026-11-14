package com.mycompany.proyectofinal.servicio;

import com.mycompany.proyectofinal.modelo.Alquiler;
import com.mycompany.proyectofinal.modelo.DetalleAccesorio;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.LineaEquipo;
import com.mycompany.proyectofinal.modelo.Liquidacion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class CalculadoraCobro {

    private static final int TOLERANCIA_MINUTOS = 15;

    public Liquidacion calcular(Alquiler alquiler, Map<Integer, String> estados, Map<Integer, Integer> extraviados, LocalDateTime horaReal) {
        Liquidacion liq = new Liquidacion(alquiler, horaReal);
        liq.getEstados().putAll(estados);
        liq.getExtraviados().putAll(extraviados);

        long minutosUso = Duration.between(alquiler.getHoraEntrega(), horaReal).toMinutes();
        int horas = (int) Math.max(1, horasRedondeadas(minutosUso));

        long minutosRetraso = Duration.between(alquiler.getHoraEstimada(), horaReal).toMinutes();
        int horasRetraso = minutosRetraso > TOLERANCIA_MINUTOS ? (int) horasRedondeadas(minutosRetraso) : 0;
        liq.setMinutosRetraso(Math.max(0, minutosRetraso));

        BigDecimal totalHoras = BigDecimal.ZERO;
        BigDecimal totalRecargo = BigDecimal.ZERO;
        BigDecimal totalReposicion = BigDecimal.ZERO;
        for (Equipo equipo : alquiler.getEquipos()) {
            boolean danado = "DANADO".equals(estados.get(equipo.getCodigo()));
            LineaEquipo linea = new LineaEquipo(equipo, horas, horasRetraso, danado);
            liq.getLineas().add(linea);
            totalHoras = totalHoras.add(linea.getSubtotal());
            totalRecargo = totalRecargo.add(linea.getRecargo());
            totalReposicion = totalReposicion.add(linea.getReposicion());
        }

        BigDecimal totalAccesorios = BigDecimal.ZERO;
        for (DetalleAccesorio detalle : alquiler.getAccesorios()) {
            detalle.setExtraviados(extraviados.getOrDefault(detalle.getAccesorio().getCodigo(), 0));
            totalAccesorios = totalAccesorios.add(detalle.getSubtotal());
            totalReposicion = totalReposicion.add(detalle.getReposicion());
        }

        BigDecimal deposito = alquiler.getDeposito();
        BigDecimal depositoAplicado = deposito.min(totalReposicion);
        BigDecimal depositoDevuelto = deposito.subtract(depositoAplicado);
        BigDecimal saldo = totalReposicion.subtract(deposito).max(BigDecimal.ZERO);
        BigDecimal montoAPagar = totalHoras.add(totalAccesorios).add(totalRecargo);
        BigDecimal total = montoAPagar.add(totalReposicion);

        liq.setTotalHoras(redondear(totalHoras));
        liq.setTotalAccesorios(redondear(totalAccesorios));
        liq.setTotalRecargo(redondear(totalRecargo));
        liq.setTotalReposicion(redondear(totalReposicion));
        liq.setTotal(redondear(total));
        liq.setDepositoAplicado(redondear(depositoAplicado));
        liq.setDepositoDevuelto(redondear(depositoDevuelto));
        liq.setSaldoGenerado(redondear(saldo));
        liq.setMontoAPagar(redondear(montoAPagar));
        return liq;
    }

    private long horasRedondeadas(long minutos) {
        return minutos <= 0 ? 0 : (minutos + 59) / 60;
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
