package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.exception.NotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionResponse;
import accenture.training.challenge2025.service.AcreditacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ExceptionHandlerController())
                .setValidator(validator)
                .build();
    }

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
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(response.id()))
            .andExpect(jsonPath("$.importe").value(importe))
            .andExpect(jsonPath("$.punto_venta_id").value(response.puntoVentaId()))
            .andExpect(jsonPath("$.nombre_punto_venta").value(response.nombrePuntoVenta()));
    }

    @Test
    void testCrearAcreditacionConImporteInvalido() throws Exception {
        var request = new AcreditacionesRequest(
            BigDecimal.ZERO,
            3
        );

        mockMvc.perform(post(Constants.ACREDITACIONES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("La solicitud contiene datos invalidos"));

        verify(service, never()).crearAcreditacion(any(AcreditacionesRequest.class));
    }

    @Test
    void testCrearAcreditacionConPuntoDeVentaInexistente() throws Exception {
        var request = new AcreditacionesRequest(
            importe,
            999
        );

        when(service.crearAcreditacion(any(AcreditacionesRequest.class)))
                .thenThrow(new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));

        mockMvc.perform(post(Constants.ACREDITACIONES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
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
            .andExpect(jsonPath("$[0].punto_venta_id").value(response.puntoVentaId()))
            .andExpect(jsonPath("$[0].nombre_punto_venta").value(response.nombrePuntoVenta()));
    }
}
