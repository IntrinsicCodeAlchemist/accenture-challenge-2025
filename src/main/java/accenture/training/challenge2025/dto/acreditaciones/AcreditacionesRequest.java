package accenture.training.challenge2025.dto.acreditaciones;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AcreditacionesRequest(
   @NotNull
   @Min(1)
   BigDecimal importe,

   @NotNull
   @JsonProperty("punto_venta_id")
   Integer puntoVentaId
) { }
