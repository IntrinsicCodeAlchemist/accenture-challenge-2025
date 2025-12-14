package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import org.json.JSONString;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionResponse;
import accenture.training.challenge2025.service.AcreditacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AcreditacionesControllerTests {
    private MockMvc mockMvc;

    @Mock
    private AcreditacionService service;

    @InjectMocks
    private AcreditacionController controller;

    @BeforeEach
    void setUp() { mockMvc = standaloneSetup(controller).build(); }

    BigDecimal importe = new BigDecimal("1500.75");

    @Test
    void testCrearAcreditacion() throws Exception {
        var request = new AcreditacionesRequest(
            importe,
            3
        );

        var response = new AcreditacionResponse(
            1L,
            importe,
            3,
            "GBA_2",
            LocalDateTime.now()
        );

        when(service.crearAcreditacion(any(AcreditacionesRequest.class))).thenReturn(response);

        mockMvc.perform(post(Constants.ACREDITACIONES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.id()))
            .andExpect(jsonPath("$.importe").value(importe))
            .andExpect(jsonPath("$.nombrePuntoVenta").value(response.nombrePuntoVenta()));
    }

    @Test
    void testObtenerAcreditaciones() throws Exception {
        var response = new AcreditacionResponse(
            1L,
            importe,
            3,
            "GBA_2",
            LocalDateTime.now()
        );

        when(service.obtenerAcreditaciones()).thenReturn(List.of(response));

        mockMvc.perform(get(Constants.ACREDITACIONES_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(response.id()))
            .andExpect(jsonPath("$[0].importe").value(importe))
            .andExpect(jsonPath("$[0].nombrePuntoVenta").value(response.nombrePuntoVenta()));
    }
}
