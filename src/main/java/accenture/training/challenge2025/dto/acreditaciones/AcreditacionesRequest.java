package accenture.training.challenge2025.dto.acreditaciones;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AcreditacionesRequest(
   @NotNull
   @DecimalMin(value = "0.0", inclusive = false)
   BigDecimal importe,

   @NotNull
   @JsonProperty("punto_venta_id")
   Integer puntoVentaId
) { }
