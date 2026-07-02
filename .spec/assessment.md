# Assessment - Brecha contra el enunciado

## Porcentaje estimado de implementación

**Estimación actualizada: 67% implementado respecto del Challenge Java 2025.**

La estimación pondera funcionalidad, calidad requerida, verificabilidad y condiciones de entrega. La aplicación contiene la mayor parte de los endpoints y la estructura principal. Luego de T0/T1/T2 la suite de tests pasa, el cache de puntos de venta tiene una base thread-safe y el CRUD de puntos de venta valida casos principales, pero aún falta robustez de costos, cobertura global, Docker/configuración y documentación final.

## Matriz de cumplimiento

| Área | Peso estimado | Estado | Observaciones |
| --- | ---: | --- | --- |
| Cache puntos de venta | 15% | 14% | Datos iniciales, CRUD, thread-safety básica, service de validación y tests de cache/controller/service agregados. |
| Cache costos/grafo | 20% | 14% | Datos iniciales, simetría y endpoints existen; faltan validaciones de PDV, casos borde y tests. |
| Camino mínimo | 15% | 10% | Dijkstra implementado; faltan pruebas, manejo robusto de inexistentes/inalcanzables/origen=destino. |
| Acreditaciones | 20% | 14% | Enriquecimiento y JPA existen; validación HTTP incompleta, tests controller fallan, config DB parcial. |
| Concurrencia | 10% | 6% | Costos y puntos de venta usan `ConcurrentHashMap`; faltan pruebas de integración/carga y decisiones de consistencia al borrar PDV. |
| Tests/cobertura/calidad | 10% | 4% | Suite pasa con 26 tests; falta cobertura de costos, Dijkstra y reporte JaCoCo. |
| Build/Docker/docs | 10% | 6% | README y Docker existen; compose tiene typo, falta guía completa de prueba y supuestos. |

Total aproximado: **67/100**.

## Hallazgos importantes

### Lo ya implementado

- Proyecto Maven con Java 21 y Spring Boot.
- Endpoints para puntos de venta: listar, obtener por id, crear, actualizar y borrar con validaciones principales mediante service.
- Cache inicial de 10 puntos de venta.
- Cache inicial de 14 costos directos.
- Grafo no dirigido para costos usando `ConcurrentHashMap`.
- Endpoints para agregar/remover costos, consultar vecinos y calcular camino mínimo.
- Algoritmo Dijkstra para camino de menor costo.
- Endpoints para crear y listar acreditaciones.
- Persistencia JPA de acreditaciones en PostgreSQL.
- DTOs con records y nombres JSON snake_case en algunos contratos.
- Manejo global básico de excepciones.
- README con diagramas Mermaid y docs adicionales en `docs/`.
- Dockerfile y docker-compose iniciales.

### Riesgos y brechas

- `PuntoDeVentaCache` ya usa `ConcurrentHashMap`; aún faltan pruebas de integración/carga bajo concurrencia real y definir efectos sobre costos al borrar PDV.
- CRUD de puntos de venta valida duplicados, inexistentes, ids nulos/nombres vacíos y consistencia path/body; quedan decisiones de consistencia con costos asociados.
- Costos no valida que origen/destino existan como puntos de venta.
- `CostosCache.addCosto` rechaza costo `0`; debe quedar explícito que el costo 0 solo aplica a A -> A, no a aristas directas.
- Vecinos de origen inexistente devuelve `nombre=null` en vez de error claro.
- Camino inalcanzable devuelve `costo_total=-1` con texto en recorrido; puede aceptarse solo si se documenta, pero REST 404 sería más claro.
- `@Valid` no está aplicado en `AcreditacionController`, por lo que las anotaciones del request pueden no ejecutarse.
- Tests actuales pasan luego de alinear `AcreditacionesControllerTests` con JSON snake_case.
- No hay tests para puntos de venta, costos ni Dijkstra.
- No hay JaCoCo ni reporte de cobertura >70%.
- `docker-compose.yml` tiene `POSTGRESS_PASSWORD` mal escrito.
- Configuración local y Docker usan nombres de base distintos.
- `application.properties` contiene credenciales hardcodeadas.
- `ddl-auto=create-drop` borra datos al reiniciar; riesgoso para persistencia demostrable.
- `target/` y `.idea/` aparecen en el árbol del repositorio; conviene revisar antes de publicar.

## Evidencia usada

- Fuente principal: repositorio local actual.
- Akashic/MCP: consultado, sin recursos disponibles.
- Verificación ejecutada: `./mvnw.cmd test`, resultado exitoso con 26 tests.
