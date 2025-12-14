package accenture.training.challenge2025.dto.acreditaciones;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AcreditacionResponse(
    Long id,
    BigDecimal importe,

    @JsonProperty("punto_venta_id")
    Integer puntoVentaId,

    @JsonProperty("nombre_punto_venta")
    String nombrePuntoVenta,

    @JsonProperty("fecha_recepcion")
    LocalDateTime fechaRecepcion
) { }
