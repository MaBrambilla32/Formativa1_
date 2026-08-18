package cl.duoc.bancoxyz.migracion.batch.processor;

import cl.duoc.bancoxyz.migracion.batch.model.Transaccion;
import cl.duoc.bancoxyz.migracion.batch.model.TransaccionEntity;
import org.springframework.batch.item.ItemProcessor;

public class TransaccionProcessor implements ItemProcessor<Transaccion, TransaccionEntity> {

    @Override
    public TransaccionEntity process(Transaccion item) throws Exception {
        
        // 1. Manejo de Errores: Descartar montos negativos o en cero (anomalías del banco)
        if (item.getMonto() <= 0) {
            System.out.println("❌ Error detectado: Monto inválido (" + item.getMonto() + ") en transacción ID " + item.getId() + ". Registro omitido.");
            return null; // Al retornar null, Spring Batch descarta automáticamente este registro defectuoso
        }

        // 2. Transformación de Datos: Estandarizar la fecha (cambiar '/' por '-')
        if (item.getFecha() != null && item.getFecha().contains("/")) {
            String fechaCorregida = item.getFecha().replace("/", "-");
            item.setFecha(fechaCorregida);
            System.out.println("🔧 Transformación: Fecha corregida para ID " + item.getId() + " -> " + fechaCorregida);
        }

        // 3. Conversión: Transformamos el objeto plano a la Entidad JPA para la base de datos
        TransaccionEntity entity = new TransaccionEntity();
        entity.setId(item.getId());
        entity.setFecha(item.getFecha());
        entity.setMonto(item.getMonto());
        entity.setTipo(item.getTipo());

        // Retornamos la entidad lista para ser guardada
        return entity;
    }
}