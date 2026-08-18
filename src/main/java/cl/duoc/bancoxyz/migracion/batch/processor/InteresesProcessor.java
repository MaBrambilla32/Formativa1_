package cl.duoc.bancoxyz.migracion.batch.processor;

import cl.duoc.bancoxyz.migracion.batch.model.Cuenta;
import cl.duoc.bancoxyz.migracion.batch.model.CuentaEntity;
import org.springframework.batch.item.ItemProcessor;

public class InteresesProcessor implements ItemProcessor<Cuenta, CuentaEntity> {

    private static final double INTERES_AHORRO = 0.005;
    private static final double INTERES_PRESTAMO = 0.01;
    private static final double INTERES_HIPOTECA = 0.008;

    @Override
    public CuentaEntity process(Cuenta cuenta) throws Exception {

        // Validar ID
        if (cuenta.getCuentaId() == null) {
            System.out.println("❌ Cuenta inválida: ID vacío. Registro omitido.");
            return null;
        }

        // Validar nombre
        if (cuenta.getNombre() == null ||
                cuenta.getNombre().trim().isEmpty() ||
                cuenta.getNombre().equalsIgnoreCase("Unknown")) {

            System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                    + ": nombre inválido. Registro omitido.");
            return null;
        }

        // Validar saldo
        if (cuenta.getSaldo() == null) {
            System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                    + ": saldo vacío. Registro omitido.");
            return null;
        }

        if (cuenta.getSaldo() < 0) {
            System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                    + ": saldo negativo. Registro omitido.");
            return null;
        }

        // Validar edad
        if (cuenta.getEdad() == null ||
                cuenta.getEdad() < 0 ||
                cuenta.getEdad() > 120) {

            System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                    + ": edad inválida (" + cuenta.getEdad()
                    + "). Registro omitido.");
            return null;
        }

        // Validar tipo
        if (cuenta.getTipo() == null) {
            System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                    + ": tipo de cuenta vacío. Registro omitido.");
            return null;
        }

        String tipo = cuenta.getTipo().trim().toLowerCase();

        double tasa;

        switch (tipo) {
            case "ahorro":
                tasa = INTERES_AHORRO;
                break;

            case "prestamo":
                tasa = INTERES_PRESTAMO;
                break;

            case "hipoteca":
                tasa = INTERES_HIPOTECA;
                break;

            default:
                System.out.println("❌ Cuenta " + cuenta.getCuentaId()
                        + ": tipo inválido (" + cuenta.getTipo()
                        + "). Registro omitido.");
                return null;
        }

        // Cálculo del interes mensual
        double interes = cuenta.getSaldo() * tasa;
        double saldoFinal = cuenta.getSaldo() + interes;

        System.out.println("💰 Cuenta " + cuenta.getCuentaId()
                + " | Tipo: " + tipo
                + " | Saldo inicial: " + cuenta.getSaldo()
                + " | Interés: " + interes
                + " | Saldo final: " + saldoFinal);

        // Crear entidad para persistir en Oracle
        CuentaEntity entity = new CuentaEntity();

        entity.setCuentaId(cuenta.getCuentaId());
        entity.setNombre(cuenta.getNombre());
        entity.setSaldo(saldoFinal);
        entity.setEdad(cuenta.getEdad());
        entity.setTipo(tipo);

        return entity;
    }
}