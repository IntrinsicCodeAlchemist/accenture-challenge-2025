# Tasks - Plan para finalizar el challenge

## Estado general estimado

**Avance estimado final: 90% del challenge.**

La base funcional está creada, pero todavía faltan robustez de concurrencia, validaciones, tests/cobertura, correcciones Docker/config, documentación ejecutable y reporte de calidad. Además, la suite de tests actual no pasa.

## Tareas priorizadas

### T0 - Estabilizar baseline de tests

- [x] Corregir `AcreditacionesControllerTests` para esperar nombres JSON reales: `nombre_punto_venta`, `punto_venta_id`, `fecha_recepcion`.
- [x] Ejecutar `./mvnw.cmd test` hasta obtener verde.
- [x] Eliminar imports no usados en tests si el IDE/build los marca.

**Criterio de aceptación:** build de tests unitarios pasa localmente.

### T1 - Hacer thread-safe el cache de puntos de venta

- [x] Reemplazar `HashMap` por `ConcurrentHashMap` en `PuntoDeVentaCache`.
- [x] Devolver snapshots inmutables o copias en `getAll()`.
- [x] Validar que `save/update/delete/findById` no expongan estado mutable.
- [x] Agregar tests unitarios de operaciones básicas y escenario concurrente simple.

**Criterio de aceptación:** operaciones concurrentes no lanzan `ConcurrentModificationException` y mantienen datos consistentes.

### T2 - Completar validaciones y semántica HTTP para puntos de venta

- [x] Crear o introducir `PuntoDeVentaService` para no validar en controller/cache.
- [x] Validar id no nulo, nombre no vacío y duplicados en creación.
- [x] Validar existencia en update/delete.
- [x] Validar consistencia entre `{id}` del path y body.
- [x] Agregar tests controller/service para CRUD completo.

**Criterio de aceptación:** CRUD responde 2xx/4xx de forma consistente y cubierta por tests.

### T3 - Robustecer costos y grafo

- [x] Validar existencia de origen/destino contra el cache de puntos de venta antes de agregar, borrar, consultar vecinos o calcular camino.
- [x] Definir y documentar comportamiento de costo 0 y `origen == destino`.
- [x] Definir y documentar respuesta para camino inalcanzable.
- [x] Asegurar que al borrar o actualizar PDV no queden aristas huérfanas, o documentar la decisión.
- [x] Agregar tests unitarios para simetría, costo negativo, puntos inexistentes, vecinos y remoción.

Notas de contrato implementado:
- Un costo directo editable debe ser mayor a cero.
- `origen == destino` en camino mínimo devuelve costo `0` y recorrido con el nombre del punto.
- Puntos inexistentes y camino inalcanzable devuelven error `404` controlado.
- Al borrar un punto de venta se remueven sus aristas salientes y entrantes del grafo de costos.

**Criterio de aceptación:** reglas del enunciado están cubiertas por tests.

### T4 - Cubrir Dijkstra con casos del challenge

- [x] Test camino directo mínimo: por ejemplo 1 -> 4 debe devolver costo 11 y recorrido CABA -> Santa Fe.
- [x] Test camino indirecto más barato cuando aplique.
- [x] Test componente desconectado o destino inalcanzable.
- [x] Test origen igual a destino con costo 0.
- [x] Test que el recorrido use nombres actuales del cache.

**Criterio de aceptación:** algoritmo probado en casos normales y borde.

### T5 - Completar acreditaciones

- [x] Agregar `@Valid` al request body del controller.
- [x] Confirmar que `@Min` sobre `BigDecimal` cumple la regla esperada o cambiar a `@DecimalMin`.
- [x] Definir si POST devuelve 200 o 201.
- [x] Agregar test de validación HTTP para importe inválido y punto de venta inexistente.
- [x] Agregar test de `obtenerAcreditaciones()` en service.

Notas de contrato implementado:
- `POST /api/acreditaciones` devuelve `201 Created` cuando persiste correctamente.
- `importe` debe ser mayor a cero mediante `@DecimalMin`.
- Errores de Bean Validation devuelven `400 Bad Request` con `ErrorResponse`.

**Criterio de aceptación:** acreditaciones validan entrada, enriquecen y persisten con errores controlados.

### T6 - Arreglar contenedores y configuración de base externa

- [x] Corregir `POSTGRESS_PASSWORD` a `POSTGRES_PASSWORD` en `docker/docker-compose.yml`.
- [x] Unificar nombre de base entre `application.properties` y Docker o documentar perfiles.
- [x] Evitar password real/hardcodeado: usar variables de entorno con defaults seguros para demo.
- [x] Revisar `Dockerfile`: el contexto `build: ..` y `COPY ../target/...` pueden fallar; ajustar para build reproducible compatible con Docker y Podman.
- [x] Documentar comandos: `./mvnw.cmd clean package`, `podman compose -f docker/docker-compose.yml up --build` o `docker compose -f docker/docker-compose.yml up --build`.
- [x] Indicar en README que Podman es una alternativa válida para la defensa cuando Docker Desktop no esté disponible en el host.

Notas de verificación:
- `podman build -f docker/Dockerfile -t challenge-2025:test .` verificado correctamente.
- `podman compose -f docker/docker-compose.yml up --build` verificado correctamente (PostgreSQL 16 Alpine + Spring Boot).
- Endpoints de acreditaciones probados con escritura y lectura contra PostgreSQL exitosamente.

**Criterio de aceptación:** la aplicación levanta con PostgreSQL externo usando Podman o Docker siguiendo README.

### T7 - Cobertura y calidad

- [x] Agregar plugin JaCoCo en Maven con threshold 70%.
- [x] Generar reporte con `./mvnw.cmd clean verify`.
- [x] Alcanzar o superar 70% de cobertura excluyendo código trivial si se justifica.
- [x] Revisar warnings relevantes de Mockito/JDK para evitar fragilidad futura.

Notas:
- Cobertura real: ~96% instrucciones, ~97% líneas (excluyendo DTOs, entidades, config, clase principal).
- No se agregó OWASP Dependency Check por simplicidad; las dependencias son de Spring Boot BOM actualizado.
- JaCoCo se integra en la fase `verify` con check automático.

**Criterio de aceptación:** existe reporte de cobertura y la suite queda por encima del objetivo.

### T8 - Documentación final para entrega

- [x] Completar README con build, ejecución local, Podman/Docker, variables requeridas y comandos de prueba.
- [x] Agregar sección con ejemplos curl para todos los casos de uso.
- [x] Documentar supuestos (12 supuestos documentados).
- [x] Documentar utilidades Java modernas usadas y versión donde aplican (tabla con 11 utilidades).
- [x] Documentar patrones de diseño aplicados (tabla con 9 patrones).
- [x] Vincular diagramas Mermaid en README.

**Criterio de aceptación:** un evaluador puede compilar, ejecutar y probar todos los casos desde cero.

### T9 - Revisión final pre-entrega

- [x] Ejecutar `git status` para asegurar solo cambios esperados.
- [x] Ejecutar `./mvnw.cmd clean verify`.
- [x] Probar manualmente endpoints principales contra la app levantada (verificado con PostgreSQL nativo y Podman Compose).
- [x] Revisar que no haya secretos reales en configuración/documentación.
- [x] Confirmar que el repositorio público no incluya artefactos innecesarios (`target/`, `.idea/`).

Notas:
- Prueba end-to-end ejecutada contra PostgreSQL tanto desde PostgreSQL nativo como desde Podman Compose.
- Acreditaciones creadas, persistidas y recuperadas correctamente desde la base de datos.

**Criterio de aceptación:** repositorio listo para publicación y defensa técnica.

## Orden sugerido de trabajo

1. T0: corregir tests actuales para tener baseline verde.
2. T1 y T2: cerrar puntos de venta, porque costos y acreditaciones dependen de ese cache.
3. T3 y T4: cerrar reglas del grafo y Dijkstra.
4. T5: cerrar acreditaciones y validación HTTP.
5. T6: asegurar ejecución reproducible.
6. T7 y T8: cobertura, calidad y documentación final.
7. T9: revisión de entrega.
