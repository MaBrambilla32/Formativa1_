package cl.duoc.bancoxyz.migracion.batch.processor;

import cl.duoc.bancoxyz.migracion.batch.model.CuentaAnual;
import cl.duoc.bancoxyz.migracion.batch.model.EstadoCuentaEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EstadoCuentaWriter implements ItemWriter<CuentaAnual> {

    private final JpaItemWriter<EstadoCuentaEntity> jpaWriter;

    public EstadoCuentaWriter(EntityManagerFactory entityManagerFactory) {
        this.jpaWriter = new JpaItemWriterBuilder<EstadoCuentaEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Override
    public void write(Chunk<? extends CuentaAnual> chunk) throws Exception {

        Map<String, EstadoCuentaEntity> estados = new HashMap<>();

        for (CuentaAnual item : chunk.getItems()) {

            LocalDate fecha = LocalDate.parse(item.getFecha());
            int anio = fecha.getYear();

            String clave = item.getCuentaId() + "-" + anio;

            EstadoCuentaEntity estado = estados.get(clave);

            if (estado == null) {

                estado = new EstadoCuentaEntity();

                // ID único para cuenta + año
                estado.setId(
                        item.getCuentaId() * 10000L + anio
                );

                estado.setCuentaId(item.getCuentaId());
                estado.setAnio(anio);
                estado.setCantidadMovimientos(0);
                estado.setTotalMovimientos(0.0);

                estados.put(clave, estado);
            }

            estado.setCantidadMovimientos(
                    estado.getCantidadMovimientos() + 1
            );

            estado.setTotalMovimientos(
                    estado.getTotalMovimientos() + item.getMonto()
            );
        }

        for (EstadoCuentaEntity estado : estados.values()) {

            System.out.println(
                    "📊 ESTADO ANUAL GENERADO"
                            + " | Cuenta: " + estado.getCuentaId()
                            + " | Año: " + estado.getAnio()
                            + " | Movimientos: "
                            + estado.getCantidadMovimientos()
                            + " | Total: "
                            + estado.getTotalMovimientos()
            );
        }

        jpaWriter.write(
        new Chunk<>(new java.util.ArrayList<>(estados.values()))
     );
    }
}