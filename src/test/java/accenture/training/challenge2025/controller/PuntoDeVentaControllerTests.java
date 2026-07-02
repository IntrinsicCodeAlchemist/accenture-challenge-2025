package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.service.PuntoDeVentaService;
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
class PuntoDeVentaControllerTests {
    private MockMvc mockMvc;

    @Mock
    private PuntoDeVentaService service;

    @InjectMocks
    private PuntoDeVentaController controller;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    void getAllReturnsPuntosDeVenta() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(new PuntoDeVenta(1, "CABA")));

        mockMvc.perform(get(Constants.PUNTO_DE_VENTA_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("CABA"));
    }

    @Test
    void getReturnsPuntoDeVenta() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(new PuntoDeVenta(1, "CABA"));

        mockMvc.perform(get(Constants.PUNTO_DE_VENTA_ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("CABA"));
    }

    @Test
    void getMissingPuntoDeVentaReturnsNotFound() throws Exception {
        when(service.obtenerPorId(999)).thenThrow(new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));

        mockMvc.perform(get(Constants.PUNTO_DE_VENTA_ENDPOINT + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
    }

    @Test
    void createReturnsCreatedPuntoDeVenta() throws Exception {
        var pdv = new PuntoDeVenta(11, "Mendoza");
        when(service.crear(any(PuntoDeVenta.class))).thenReturn(pdv);

        mockMvc.perform(post(Constants.PUNTO_DE_VENTA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pdv)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pdv.id()))
                .andExpect(jsonPath("$.nombre").value(pdv.nombre()));
    }

    @Test
    void createInvalidPuntoDeVentaReturnsBadRequest() throws Exception {
        var pdv = new PuntoDeVenta(null, "");
        when(service.crear(any(PuntoDeVenta.class)))
                .thenThrow(new BadRequestException(Constants.PUNTO_DE_VENTA_INVALID_EXCEPTION));

        mockMvc.perform(post(Constants.PUNTO_DE_VENTA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pdv)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_INVALID_EXCEPTION));
    }

    @Test
    void updateReturnsUpdatedPuntoDeVenta() throws Exception {
        var pdv = new PuntoDeVenta(11, "Mendoza Centro");
        when(service.actualizar(11, pdv)).thenReturn(pdv);

        mockMvc.perform(put(Constants.PUNTO_DE_VENTA_ENDPOINT + "/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pdv)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pdv.id()))
                .andExpect(jsonPath("$.nombre").value(pdv.nombre()));
    }

    @Test
    void updateWithMismatchedIdReturnsBadRequest() throws Exception {
        var pdv = new PuntoDeVenta(12, "Mendoza");
        when(service.actualizar(11, pdv))
                .thenThrow(new BadRequestException(Constants.PUNTO_DE_VENTA_ID_MISMATCH_EXCEPTION));

        mockMvc.perform(put(Constants.PUNTO_DE_VENTA_ENDPOINT + "/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pdv)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_ID_MISMATCH_EXCEPTION));
    }

    @Test
    void deleteExistingPuntoDeVentaReturnsNoContent() throws Exception {
        mockMvc.perform(delete(Constants.PUNTO_DE_VENTA_ENDPOINT + "/11"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMissingPuntoDeVentaReturnsNotFound() throws Exception {
        doThrow(new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION))
                .when(service).borrar(999);

        mockMvc.perform(delete(Constants.PUNTO_DE_VENTA_ENDPOINT + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
    }
}
