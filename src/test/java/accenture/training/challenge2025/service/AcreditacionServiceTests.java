package accenture.training.challenge2025.service;

import accenture.training.challenge2025.cache.PuntoDeVentaCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionResponse;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.entity.Acreditacion;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.repository.AcreditacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcreditacionServiceTests {
    @Mock
    private PuntoDeVentaCache pdvCache;

    @Mock
    private AcreditacionRepository repo;

    @InjectMocks
    private AcreditacionService acreditacionService;

    BigDecimal importe = new BigDecimal("1500.75");

    @Test
    void testCrearAcreditacion() {
        var puntoDeVenta = new PuntoDeVenta(3, "GBA_2");

        var request = new AcreditacionesRequest(importe, puntoDeVenta.id());
        var mockAcreditacion = Acreditacion
                                .builder()
                                .id(1L)
                                .importe(importe)
                                .puntoVentaId(puntoDeVenta.id())
                                .nombrePuntoVenta(puntoDeVenta.nombre())
                                .fechaRecepcion(LocalDateTime.now())
                                .build();

        when(pdvCache.findById(puntoDeVenta.id())).thenReturn(Optional.of(puntoDeVenta));
        when(repo.save(any(Acreditacion.class))).thenReturn(mockAcreditacion);

        AcreditacionResponse response = acreditacionService.crearAcreditacion(request);

        assertNotNull(response);
        assertEquals(mockAcreditacion.getId(), response.id());
        assertEquals(importe, response.importe());
        assertEquals(puntoDeVenta.nombre(), response.nombrePuntoVenta());

        verify(pdvCache, times(1)).findById(puntoDeVenta.id());
        verify(repo, times(1)).save(any(Acreditacion.class));
    }

    @Test
    void testCrearAcreditacionConPdvInexistente() {
        AcreditacionesRequest request = new AcreditacionesRequest(importe, 999);

        when(pdvCache.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
            () -> acreditacionService.crearAcreditacion(request));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
    }
}


