# Migración Batch - Banco XYZ

Este proyecto es una solución basada en **Spring Batch** diseñada para el Banco XYZ. Su objetivo principal es procesar, limpiar y migrar datos bancarios históricos (transacciones diarias, cálculo de intereses y estados de cuenta) desde archivos CSV hacia una base de datos relacional (Oracle Cloud).

## Estructura del Proyecto

El código fuente está organizado bajo el paquete `cl.duoc.bancoxyz.migracion.batch` y se divide en:

*   **`config/`**: Contiene la clase `BatchConfig.java`, encargada de orquestar los Jobs, Steps, Readers y Writers del proceso batch.
*   **`model/`**: Almacena las entidades y DTOs, como `Transaccion` (representación del CSV) y `TransaccionEntity` (entidad JPA para la base de datos).
*   **`processor/`**: Incluye la lógica de negocio y validación, como `TransaccionProcessor.java`, que estandariza fechas y omite transacciones con montos en cero o negativos.
*   **`wallet/`**: Contiene las credenciales de seguridad y configuración de red para la conexión con Autonomous Database en Oracle Cloud.
*   **`src/main/resources/data/`**: Directorio donde se ubican los archivos `.csv` de origen agrupados por semanas.

## Instrucciones de Ejecución

Para ejecutar el proyecto y procesar los archivos batch en tu entorno local, sigue estos pasos:

1.  Asegúrate de tener instalado **Java 17** y **Maven**.
2.  Verifica que los archivos de la Wallet de Oracle estén correctamente ubicados en la carpeta raíz `wallet/`.
3.  Abre una terminal en la raíz del proyecto.
4.  Ejecuta el siguiente comando para iniciar la aplicación omitiendo la fase de pruebas:

    ```bash
    .\mvnw spring-boot:run -Dmaven.test.skip=true
    ```

5.  Observa la consola para verificar los logs del procesador detectando anomalías y las sentencias de inserción en la base de datos.