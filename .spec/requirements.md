# Requirements - Accenture Challenge Java 2025

## Alcance

La aplicación debe exponer una API HTTP en Java para administrar caches en memoria de puntos de venta y costos entre puntos de venta, registrar acreditaciones enriquecidas en una base de datos externa, y entregar documentación y pruebas suficientes para defender la solución.

## Estado actual resumido

- Stack detectado: Java 21, Spring Boot 4.0.0, Maven, Spring MVC, Spring Data JPA, Spring Security, PostgreSQL, Lombok, springdoc-openapi.
- Módulos implementados: puntos de venta, costos, acreditaciones, manejo global de errores básico, Docker parcial, documentación Mermaid parcial.
- Estado de verificación local: `./mvnw.cmd test` pasa con 8 tests luego de corregir T0/T1.

## Requerimientos funcionales

### RF-01 - Cache inicial de puntos de venta

**User story:** Como consumidor de la API, quiero que la aplicación arranque con los 10 puntos de venta del enunciado para poder usarlos sin carga manual.

**Acceptance criteria:**
- GIVEN la aplicación recién iniciada WHEN consulto todos los puntos de venta THEN se devuelven los ids 1 a 10 con los nombres CABA, GBA_1, GBA_2, Santa Fe, Córdoba, Misiones, Salta, Chubut, Santa Cruz y Catamarca.
- GIVEN la aplicación recibe tráfico concurrente WHEN se leen y modifican puntos de venta THEN no se producen errores de concurrencia ni respuestas corruptas.

**Estado:** Parcial alto. Datos iniciales presentes; cache migrado a `ConcurrentHashMap` con snapshots inmutables básicos. Quedan validaciones de negocio.

### RF-02 - CRUD de puntos de venta

**User story:** Como consumidor de la API, quiero listar, crear, actualizar y borrar puntos de venta para administrar el cache en memoria.

**Acceptance criteria:**
- GET colección devuelve todos los puntos.
- POST crea un nuevo punto con id y nombre válidos.
- PUT actualiza un punto existente y valida consistencia entre path/body.
- DELETE elimina un punto existente.
- Operaciones inválidas devuelven errores HTTP consistentes.

**Estado:** Parcial. Endpoints existen; faltan validaciones, tests, semántica HTTP más estricta y concurrencia.

### RF-03 - Cache inicial de costos

**User story:** Como consumidor de la API, quiero que la aplicación arranque con las 14 aristas del enunciado para consultar conectividad y caminos mínimos.

**Acceptance criteria:**
- GIVEN la aplicación recién iniciada WHEN consulto vecinos de cada punto THEN aparecen las conexiones iniciales simétricas y con costo correcto.
- El costo A-B siempre coincide con B-A.

**Estado:** Mayormente implementado.

### RF-04 - Administración de costos directos

**User story:** Como consumidor de la API, quiero cargar y remover costos directos entre dos puntos para modificar el grafo en memoria.

**Acceptance criteria:**
- POST agrega o actualiza una conexión directa única entre A y B.
- DELETE remueve la conexión directa en ambos sentidos.
- No se aceptan costos negativos.
- El costo de A hacia A es 0 y no se guarda como arista directa editable.
- No se aceptan puntos de venta inexistentes.

**Estado:** Parcial. Simetría y cache concurrente implementados; faltan validaciones de existencia, tests y decisión explícita sobre costo 0.

### RF-05 - Consulta de vecinos directos

**User story:** Como consumidor de la API, quiero consultar los puntos directamente conectados a un origen y sus costos para entender la conectividad local.

**Acceptance criteria:**
- GET por origen devuelve nombre del origen, ids/nombres de vecinos y costo directo.
- Origen inexistente devuelve 404 o error documentado.
- Origen sin vecinos devuelve lista vacía válida.

**Estado:** Parcial. Endpoint existe; origen inexistente devuelve nombre null con lista vacía.

### RF-06 - Camino de costo mínimo

**User story:** Como consumidor de la API, quiero consultar el camino de menor costo entre dos puntos para obtener costo total y recorrido por nombres.

**Acceptance criteria:**
- Se usa un algoritmo correcto para pesos no negativos.
- Respuesta incluye costo total y recorrido usando nombres del cache de puntos de venta.
- Si destino es inalcanzable, la API devuelve error o respuesta documentada.
- Si origen o destino no existen, se devuelve error controlado.
- Se cubren casos de camino directo no mínimo, mismo origen/destino e inalcanzable.

**Estado:** Parcial. Dijkstra implementado; faltan validaciones, tests y definición HTTP para inalcanzable.

### RF-07 - Recepción y enriquecimiento de acreditaciones

**User story:** Como sistema externo, quiero enviar importe e identificador de punto de venta para que la aplicación enriquezca y persista la acreditación.

**Acceptance criteria:**
- POST recibe `importe` y `punto_venta_id`.
- Se valida que importe sea positivo y punto de venta exista.
- Se agrega `fecha_recepcion` del momento de recepción.
- Se agrega `nombre_punto_venta` desde el cache.
- Si el punto no existe, falla con error controlado.

**Estado:** Parcial alto. Servicio implementado; faltan `@Valid` en controller, tests corregidos y validación de null/importe en capa HTTP.

### RF-08 - Persistencia y consulta de acreditaciones

**User story:** Como consumidor de la API, quiero consultar las acreditaciones persistidas para auditar lo recibido.

**Acceptance criteria:**
- Las acreditaciones se guardan en una BBDD externa a la aplicación.
- GET devuelve todas las acreditaciones persistidas con importe, punto de venta, fecha y nombre.
- Configuración local y Docker usan la misma base esperada o lo documentan claramente.

**Estado:** Parcial. JPA/PostgreSQL implementado; Docker/config tienen inconsistencias y falta perfil de test/integración.

## Requerimientos no funcionales

### RNF-01 - Concurrencia
- La solución debe soportar alto grado de concurrencia sin errores causados por caches mutables.
- Estado: parcialmente cumplido. `PuntoDeVentaCache` ya usa `ConcurrentHashMap` y snapshots inmutables; faltan pruebas de integración/carga y reglas de consistencia más completas.

### RNF-02 - Tests y cobertura
- Deben existir tests unitarios y se desea cobertura mayor al 70%.
- Estado: insuficiente pero estable. Hay tests de acreditaciones y cache de puntos de venta; la suite pasa, pero aún falta cobertura de costos, Dijkstra, controllers de PDV y reporte >70%.

### RNF-03 - Calidad, seguridad y dependencias
- Evitar smells, bugs, issues y vulnerabilidades críticas conocidas cuando existan versiones corregidas.
- Estado: pendiente. No hay reporte automatizado de cobertura/calidad/vulnerabilidades.

### RNF-04 - Documentación de build, ejecución y pruebas
- Debe documentarse cómo compilar, ejecutar, correr Docker y probar casos de uso.
- Estado: parcial. README describe arquitectura pero no cubre scripts/comandos/casos exhaustivos.

### RNF-05 - Diagramas nativos del repositorio
- Deben usarse diagramas renderizables en Markdown/Mermaid.
- Estado: parcial alto. Hay Mermaid en README y `docs/`.

### RNF-06 - Versiones modernas de Java y patrones
- Se deben indicar utilidades modernas de Java y patrones usados.
- Estado: parcial. Se usan records, `var`, streams, builder, repository/service/controller; falta documentarlo formalmente.

## Supuestos a formalizar

- Las operaciones de cache son in-memory y se pierden al reiniciar.
- Agregar costo entre dos puntos existentes actualiza el costo directo si ya existía.
- Eliminar un punto de venta debería definir qué sucede con sus costos y acreditaciones históricas.
- El camino inalcanzable puede representarse como 404 o como respuesta con costo `-1`; debe elegirse y documentarse.
- La fecha de recepción se guarda en hora local del servidor salvo que se adopte `Instant`/UTC.
