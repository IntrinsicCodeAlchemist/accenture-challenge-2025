package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.costos.CaminoMinimoResponse;
import accenture.training.challenge2025.dto.costos.CostoRequest;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.service.CostosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CostosControllerTests {
    private MockMvc mockMvc;

    @Mock
    private CostosService service;

    @InjectMocks
    private CostosController controller;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    void addCostoReturnsOk() throws Exception {
        var request = new CostoRequest(1, 2, 2);

        mockMvc.perform(post(Constants.COSTOS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addCostoInvalidReturnsBadRequest() throws Exception {
        doThrow(new BadRequestException(Constants.COSTO_INVALID_EXCEPTION))
                .when(service).agregarCosto(any(CostoRequest.class));

        mockMvc.perform(post(Constants.COSTOS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new CostoRequest(1, 2, -1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constants.COSTO_INVALID_EXCEPTION));
    }

    @Test
    void deleteCostoWithMissingPuntoDeVentaReturnsNotFound() throws Exception {
        doThrow(new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION))
                .when(service).removerCosto(1, 999);

        mockMvc.perform(delete(Constants.COSTOS_ENDPOINT + "/1/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
    }

    @Test
    void caminoReturnsMinimumPath() throws Exception {
        when(service.calcularCaminoMinimo(1, 1))
                .thenReturn(new CaminoMinimoResponse(0, List.of("CABA")));

        mockMvc.perform(get(Constants.COSTOS_ENDPOINT + "/camino/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costo_total").value(0))
                .andExpect(jsonPath("$.recorrido[0]").value("CABA"));
    }

    @Test
    void caminoWithoutPathReturnsNotFound() throws Exception {
        when(service.calcularCaminoMinimo(1, 11))
                .thenThrow(new NotFoundException(Constants.COSTOS_CAMINO_NOT_FOUND));

        mockMvc.perform(get(Constants.COSTOS_ENDPOINT + "/camino/1/11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.COSTOS_CAMINO_NOT_FOUND));
    }
}
