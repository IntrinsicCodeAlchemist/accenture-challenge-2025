package accenture.training.challenge2025.dto.costos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CaminoMinimoResponse(
    @JsonProperty("costo_total")
    int costoTotal,

    List<String> recorrido
) { }
