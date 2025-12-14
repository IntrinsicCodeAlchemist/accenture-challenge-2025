package accenture.training.challenge2025.dto.costos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CostosOrigenResponse(
    String nombre,

    @JsonProperty("vecinos")
    List<Vecino> vecinoDto
) { }
