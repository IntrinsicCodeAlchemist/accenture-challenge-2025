# Assessment - Brecha contra el enunciado

## Porcentaje estimado de implementación

**Estimación final: 90% implementado respecto del Challenge Java 2025.**

La estimación pondera funcionalidad, calidad requerida, verificabilidad y condiciones de entrega. Luego de T0/T1/T2/T3/T4/T5/T6/T7/T8 la suite de tests pasa, puntos de venta/costos/Dijkstra están robustecidos, acreditaciones validan entrada HTTP, imagen de contenedor reproducible con Podman/Docker, cobertura documentada por JaCoCo y README completo con supuestos, patrones y utilidades modernas. Falta únicamente validación end-to-end contra PostgreSQL real, que queda a cargo del usuario.

## Matriz de cumplimiento

| Área | Peso estimado | Estado | Observaciones |
| --- | ---: | --- | --- |
| Cache puntos de venta | 15% | 14% | Datos iniciales, CRUD, thread-safety básica, service de validación y tests de cache/controller/service agregados. |
| Cache costos/grafo | 20% | 19% | Datos iniciales, simetría, endpoints, validación de PDV/costo, limpieza de aristas al borrar PDV y tests de service/controller. |
| Camino mínimo | 15% | 15% | Dijkstra implementado y probado con ruta directa mínima, ruta indirecta más barata, mismo origen, inalcanzable y nombres actuales del cache. |
| Acreditaciones | 20% | 19% | Enriquecimiento y JPA existen; POST valida con `@Valid`, devuelve 201, maneja importe inválido y PDV inexistente con tests. Falta prueba de integración con PostgreSQL real. |
| Concurrencia | 10% | 8% | Costos y puntos de venta usan `ConcurrentHashMap` con snapshots inmutables; limpieza de aristas al borrar PDV. |
| Tests/cobertura/calidad | 10% | 9% | Suite pasa con 49 tests; JaCoCo integrado con ~96% instrucciones y check >70%. |
| Build/Podman-Docker/docs | 10% | 10% | Dockerfile multi-stage, compose, configuración por variables, README completo con comandos, supuestos, patrones y utilidades modernas. |

Total aproximado: **90/100**.

## Hallazgos importantes

### Lo ya implementado

- Proyecto Maven con Java 21 y Spring Boot.
- Endpoints para puntos de venta: listar, obtener por id, crear, actualizar y borrar con validaciones principales mediante service.
- Cache inicial de 10 puntos de venta.
- Cache inicial de 14 costos directos.
- Grafo no dirigido para costos usando `ConcurrentHashMap`.
- Endpoints para agregar/remover costos, consultar vecinos y calcular camino mínimo con validación de puntos existentes.
- Algoritmo Dijkstra para camino de menor costo cubierto con tests representativos.
- Endpoints para crear y listar acreditaciones con validación HTTP y respuesta 201 al crear.
- Persistencia JPA de acreditaciones en PostgreSQL.
- DTOs con records y nombres JSON snake_case en algunos contratos.
- Manejo global básico de excepciones.
- README con diagramas Mermaid y docs adicionales en `docs/`.
- Dockerfile multi-stage compatible con Podman/Docker y compose para PostgreSQL externo.

### Riesgos y brechas

- `PuntoDeVentaCache` ya usa `ConcurrentHashMap`; aún faltan pruebas de integración/carga bajo concurrencia real.
- CRUD de puntos de venta valida duplicados, inexistentes, ids nulos/nombres vacíos y consistencia path/body; quedan decisiones de consistencia con costos asociados.
- Costos ya valida que origen/destino existan como puntos de venta.
- El contrato implementado define que un costo directo editable debe ser mayor a cero; A -> A se resuelve como costo 0 solo en camino mínimo.
- Vecinos de origen inexistente devuelve 404 controlado.
- Camino inalcanzable devuelve 404 controlado.
- `@Valid` está aplicado en `AcreditacionController` y los errores de Bean Validation devuelven 400 controlado.
- Tests actuales pasan luego de alinear `AcreditacionesControllerTests` con JSON snake_case.
- No hay tests para puntos de venta, costos ni Dijkstra.
- No hay JaCoCo ni reporte de cobertura >70%.
- `compose up` completo no fue ejecutado en esta máquina porque falta proveedor de Compose para Podman (`podman-compose` o compatible).
- `target/` y `.idea/` aparecen en el árbol del repositorio; conviene revisar antes de publicar.

## Evidencia usada

- Fuente principal: repositorio local actual.
- Akashic/MCP: consultado, sin recursos disponibles.
- Verificación ejecutada: `./mvnw.cmd test`, resultado exitoso con 49 tests.
- Verificación de contenedor: `podman build -f docker/Dockerfile -t challenge-2025:test .`, resultado exitoso.
