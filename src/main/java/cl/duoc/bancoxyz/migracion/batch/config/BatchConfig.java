package cl.duoc.bancoxyz.migracion.batch.config;

import cl.duoc.bancoxyz.migracion.batch.model.Cuenta;
import cl.duoc.bancoxyz.migracion.batch.model.CuentaAnual;
import cl.duoc.bancoxyz.migracion.batch.model.CuentaEntity;
import cl.duoc.bancoxyz.migracion.batch.model.Transaccion;
import cl.duoc.bancoxyz.migracion.batch.model.TransaccionEntity;
import cl.duoc.bancoxyz.migracion.batch.processor.EstadoCuentaWriter;
import cl.duoc.bancoxyz.migracion.batch.processor.InteresesProcessor;
import cl.duoc.bancoxyz.migracion.batch.processor.TransaccionProcessor;
import cl.duoc.bancoxyz.migracion.batch.processor.EstadosCuentaProcessor;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    // =========================================================
    // JOB 1 - REPORTE DE TRANSACCIONES DIARIAS
    // =========================================================

    @Bean
    public FlatFileItemReader<Transaccion> transaccionItemReader() {

        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(
                        new ClassPathResource(
                                "data/semana_1/transacciones.csv"
                        )
                )
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .linesToSkip(1)
                .fieldSetMapper(
                        new BeanWrapperFieldSetMapper<Transaccion>() {{
                            setTargetType(Transaccion.class);
                        }}
                )
                .build();
    }

    @Bean
    public TransaccionProcessor transaccionProcessor() {
        return new TransaccionProcessor();
    }

    @Bean
    public JpaItemWriter<TransaccionEntity> transaccionItemWriter(
            EntityManagerFactory entityManagerFactory) {

        return new JpaItemWriterBuilder<TransaccionEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step transaccionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> reader,
            TransaccionProcessor processor,
            JpaItemWriter<TransaccionEntity> writer) {

        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, TransaccionEntity>chunk(
                        10,
                        transactionManager
                )
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importarTransaccionesJob(
            JobRepository jobRepository,
            Step transaccionStep) {

        return new JobBuilder(
                "importarTransaccionesJob",
                jobRepository
        )
                .start(transaccionStep)
                .build();
    }


    // =========================================================
    // JOB 2 - CÁLCULO DE INTERESES MENSUALES
    // =========================================================

    @Bean
    public FlatFileItemReader<Cuenta> cuentaItemReader() {

        return new FlatFileItemReaderBuilder<Cuenta>()
                .name("cuentaReader")
                .resource(
                        new ClassPathResource(
                                "data/semana_2/intereses.csv"
                        )
                )
                .delimited()
                .names(
                        "cuentaId",
                        "nombre",
                        "saldo",
                        "edad",
                        "tipo"
                )
                .linesToSkip(1)
                .fieldSetMapper(
                        new BeanWrapperFieldSetMapper<Cuenta>() {{
                            setTargetType(Cuenta.class);
                        }}
                )
                .build();
    }

    @Bean
    public InteresesProcessor interesesProcessor() {
        return new InteresesProcessor();
    }

    @Bean
    public JpaItemWriter<CuentaEntity> cuentaItemWriter(
            EntityManagerFactory entityManagerFactory) {

        return new JpaItemWriterBuilder<CuentaEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step interesesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Cuenta> cuentaItemReader,
            InteresesProcessor interesesProcessor,
            JpaItemWriter<CuentaEntity> cuentaItemWriter) {

        return new StepBuilder("interesesStep", jobRepository)
                .<Cuenta, CuentaEntity>chunk(
                        10,
                        transactionManager
                )
                .reader(cuentaItemReader)
                .processor(interesesProcessor)
                .writer(cuentaItemWriter)
                .build();
    }

    @Bean
    public Job calcularInteresesJob(
            JobRepository jobRepository,
            Step interesesStep) {

        return new JobBuilder(
                "calcularInteresesJob",
                jobRepository
        )
                .start(interesesStep)
                .build();
    }


    // =========================================================
    // JOB 3 - GENERACIÓN DE ESTADOS DE CUENTA ANUALES
    // =========================================================

    // LECTOR
    @Bean
    public FlatFileItemReader<CuentaAnual> cuentaAnualItemReader() {

        return new FlatFileItemReaderBuilder<CuentaAnual>()
                .name("cuentaAnualReader")
                .resource(
                        new ClassPathResource(
                                "data/semana_1/cuentas_anuales.csv"
                        )
                )
                .delimited()
                .names(
                        "cuentaId",
                        "fecha",
                        "transaccion",
                        "monto",
                        "descripcion"
                )
                .linesToSkip(1)
                .fieldSetMapper(
                        new BeanWrapperFieldSetMapper<CuentaAnual>() {{
                            setTargetType(CuentaAnual.class);
                        }}
                )
                .build();
    }

    // PROCESSOR
    @Bean
    public EstadosCuentaProcessor estadosCuentaProcessor() {
        return new EstadosCuentaProcessor();
    }

    // WRITER
    @Bean
    public EstadoCuentaWriter estadoCuentaWriter(
            EntityManagerFactory entityManagerFactory) {

        return new EstadoCuentaWriter(entityManagerFactory);
    }

    // STEP
    @Bean
    public Step estadosCuentaStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CuentaAnual> cuentaAnualItemReader,
            EstadosCuentaProcessor estadosCuentaProcessor,
            EstadoCuentaWriter estadoCuentaWriter) {

        return new StepBuilder(
                "estadosCuentaStep",
                jobRepository
        )
                .<CuentaAnual, CuentaAnual>chunk(
                        100,
                        transactionManager
                )
                .reader(cuentaAnualItemReader)
                .processor(estadosCuentaProcessor)
                .writer(estadoCuentaWriter)
                .build();
    }

    // JOB
    @Bean
    public Job generarEstadosCuentaJob(
            JobRepository jobRepository,
            Step estadosCuentaStep) {

        return new JobBuilder(
                "generarEstadosCuentaJob",
                jobRepository
        )
                .start(estadosCuentaStep)
                .build();
    }
}