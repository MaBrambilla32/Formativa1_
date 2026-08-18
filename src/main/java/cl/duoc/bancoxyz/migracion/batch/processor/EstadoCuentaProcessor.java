package cl.duoc.bancoxyz.migracion.batch.processor;

import cl.duoc.bancoxyz.migracion.batch.model.CuentaAnual;
import cl.duoc.bancoxyz.migracion.batch.model.EstadoCuentaEntity;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EstadoCuentaProcessor
        implements ItemProcessor<CuentaAnual, EstadoCuentaEntity> {

    private final Map<String, EstadoCuentaEntity> acumulados = new HashMap<>();

    private long siguienteId = 1;

    @Override
    public EstadoCuentaEntity process(CuentaAnual item) throws Exception {

        LocalDate fecha = LocalDate.parse(item.getFecha());

        int anio = fecha.getYear();

        String clave = item.getCuentaId() + "-" + anio;

        EstadoCuentaEntity estado = acumulados.get(clave);

        if (estado == null) {

            estado = new EstadoCuentaEntity();

            estado.setId(siguienteId++);
            estado.setCuentaId(item.getCuentaId());
            estado.setAnio(anio);
            estado.setCantidadMovimientos(0);
            estado.setTotalMovimientos(0.0);

            acumulados.put(clave, estado);
        }

        estado.setCantidadMovimientos(
                estado.getCantidadMovimientos() + 1
        );

        estado.setTotalMovimientos(
                estado.getTotalMovimientos() + item.getMonto()
        );

        System.out.println(
                "📊 Estado anual | Cuenta: "
                        + item.getCuentaId()
                        + " | Año: "
                        + anio
                        + " | Movimientos: "
                        + estado.getCantidadMovimientos()
                        + " | Total: "
                        + estado.getTotalMovimientos()
        );

        return estado;
    }
}