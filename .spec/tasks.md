# Tasks - Plan para finalizar el challenge

## Estado general estimado

**Avance estimado actual: 67% del challenge.**

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

- [ ] Validar existencia de origen/destino contra el cache de puntos de venta antes de agregar, borrar, consultar vecinos o calcular camino.
- [ ] Definir y documentar comportamiento de costo 0 y `origen == destino`.
- [ ] Definir y documentar respuesta para camino inalcanzable.
- [ ] Asegurar que al borrar o actualizar PDV no queden aristas huérfanas, o documentar la decisión.
- [ ] Agregar tests unitarios para simetría, costo negativo, puntos inexistentes, vecinos y remoción.

**Criterio de aceptación:** reglas del enunciado están cubiertas por tests.

### T4 - Cubrir Dijkstra con casos del challenge

- [ ] Test camino directo mínimo: por ejemplo 1 -> 4 debe devolver costo 11 y recorrido CABA -> Santa Fe.
- [ ] Test camino indirecto más barato cuando aplique.
- [ ] Test componente desconectado o destino inalcanzable.
- [ ] Test origen igual a destino con costo 0.
- [ ] Test que el recorrido use nombres actuales del cache.

**Criterio de aceptación:** algoritmo probado en casos normales y borde.

### T5 - Completar acreditaciones

- [ ] Agregar `@Valid` al request body del controller.
- [ ] Confirmar que `@Min` sobre `BigDecimal` cumple la regla esperada o cambiar a `@DecimalMin`.
- [ ] Definir si POST devuelve 200 o 201.
- [ ] Agregar test de validación HTTP para importe inválido y punto de venta inexistente.
- [ ] Agregar test de `obtenerAcreditaciones()` en service.

**Criterio de aceptación:** acreditaciones validan entrada, enriquecen y persisten con errores controlados.

### T6 - Arreglar Docker y configuración de base externa

- [ ] Corregir `POSTGRESS_PASSWORD` a `POSTGRES_PASSWORD` en `docker/docker-compose.yml`.
- [ ] Unificar nombre de base entre `application.properties` y Docker o documentar perfiles.
- [ ] Evitar password real/hardcodeado: usar variables de entorno con defaults seguros para demo.
- [ ] Revisar `Dockerfile`: el contexto `build: ..` y `COPY ../target/...` pueden fallar; ajustar para build reproducible.
- [ ] Documentar comandos: `./mvnw.cmd clean package`, `docker compose -f docker/docker-compose.yml up --build`.

**Criterio de aceptación:** la aplicación levanta con PostgreSQL externo por Docker siguiendo README.

### T7 - Cobertura y calidad

- [ ] Agregar plugin JaCoCo en Maven.
- [ ] Generar reporte con `./mvnw.cmd clean verify`.
- [ ] Alcanzar o superar 70% de cobertura excluyendo código trivial si se justifica.
- [ ] Agregar task Maven o documentación para reporte de dependencias/vulnerabilidades si se decide usar OWASP Dependency Check o equivalente.
- [ ] Revisar warnings relevantes de Mockito/JDK para evitar fragilidad futura.

**Criterio de aceptación:** existe reporte de cobertura y la suite queda por encima del objetivo.

### T8 - Documentación final para entrega

- [ ] Completar README con build, ejecución local, Docker, variables requeridas y comandos de prueba.
- [ ] Agregar colección curl/Postman o sección con ejemplos para todos los casos de uso.
- [ ] Documentar supuestos.
- [ ] Documentar utilidades Java modernas usadas y versión donde aplican.
- [ ] Documentar patrones de diseño aplicados.
- [ ] Vincular diagramas Mermaid en README o mantenerlos renderizables en `docs/`.

**Criterio de aceptación:** un evaluador puede compilar, ejecutar y probar todos los casos desde cero.

### T9 - Revisión final pre-entrega

- [ ] Ejecutar `git status` para asegurar solo cambios esperados.
- [ ] Ejecutar `./mvnw.cmd clean verify`.
- [ ] Probar manualmente endpoints principales contra la app levantada.
- [ ] Revisar que no haya secretos reales en configuración/documentación.
- [ ] Confirmar que el repositorio público no incluya artefactos innecesarios (`target/`, `.idea/`) si no corresponde.

**Criterio de aceptación:** repositorio listo para publicación y defensa técnica.

## Orden sugerido de trabajo

1. T0: corregir tests actuales para tener baseline verde.
2. T1 y T2: cerrar puntos de venta, porque costos y acreditaciones dependen de ese cache.
3. T3 y T4: cerrar reglas del grafo y Dijkstra.
4. T5: cerrar acreditaciones y validación HTTP.
5. T6: asegurar ejecución reproducible.
6. T7 y T8: cobertura, calidad y documentación final.
7. T9: revisión de entrega.
