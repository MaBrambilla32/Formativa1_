package cl.duoc.bancoxyz.migracion.batch.processor;

import cl.duoc.bancoxyz.migracion.batch.model.CuentaAnual;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EstadosCuentaProcessor
        implements ItemProcessor<CuentaAnual, CuentaAnual> {

    @Override
    public CuentaAnual process(CuentaAnual item) throws Exception {

        // Validar cuenta
        if (item.getCuentaId() == null) {
            System.out.println(
                    "❌ Cuenta inválida: ID vacío. Registro omitido."
            );
            return null;
        }

        // Validar fecha
        if (item.getFecha() == null ||
                item.getFecha().trim().isEmpty()) {

            System.out.println(
                    "❌ Cuenta " + item.getCuentaId()
                            + ": fecha vacía. Registro omitido."
            );
            return null;
        }

        // Validar y obtener año
        try {

            LocalDate fecha = LocalDate.parse(
                    item.getFecha(),
                    DateTimeFormatter.ISO_LOCAL_DATE
            );

            System.out.println(
                    "📅 Cuenta " + item.getCuentaId()
                            + " | Fecha válida: " + fecha
                            + " | Año: " + fecha.getYear()
            );

        } catch (DateTimeParseException e) {

            System.out.println(
                    "❌ Cuenta " + item.getCuentaId()
                            + ": fecha inválida ("
                            + item.getFecha()
                            + "). Registro omitido."
            );

            return null;
        }

        // Validar monto
        if (item.getMonto() == null) {

            System.out.println(
                    "❌ Cuenta " + item.getCuentaId()
                            + ": monto vacío. Registro omitido."
            );

            return null;
        }

        // Los montos en cero se consideran inválidos
        if (item.getMonto() == 0) {

            System.out.println(
                    "❌ Cuenta " + item.getCuentaId()
                            + ": monto cero. Registro omitido."
            );

            return null;
        }

        // Validar tipo de transacción
        if (item.getTransaccion() == null ||
                item.getTransaccion().trim().isEmpty()) {

            System.out.println(
                    "❌ Cuenta " + item.getCuentaId()
                            + ": tipo de transacción vacío. Registro omitido."
            );

            return null;
        }

        // La descripción puede faltar.
        // No descartamos el movimiento por este motivo.
        if (item.getDescripcion() == null ||
                item.getDescripcion().trim().isEmpty()) {

            System.out.println(
                    "⚠️ Cuenta " + item.getCuentaId()
                            + ": descripción vacía."
            );
        }

        // Normalizar tipo de transacción
        item.setTransaccion(
                item.getTransaccion().trim().toLowerCase()
        );

        System.out.println(
                "✅ Movimiento válido | Cuenta: "
                        + item.getCuentaId()
                        + " | Tipo: "
                        + item.getTransaccion()
                        + " | Monto: "
                        + item.getMonto()
        );

        return item;
    }
}