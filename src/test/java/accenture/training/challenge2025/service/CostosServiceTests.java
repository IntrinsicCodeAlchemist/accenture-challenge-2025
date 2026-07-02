package accenture.training.challenge2025.service;

import accenture.training.challenge2025.cache.CostosCache;
import accenture.training.challenge2025.cache.PuntoDeVentaCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.costos.CostoRequest;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostosServiceTests {
    private CostosCache costosCache;
    private PuntoDeVentaCache puntoDeVentaCache;
    private CostosService service;

    @BeforeEach
    void setUp() {
        costosCache = new CostosCache();
        costosCache.init();
        puntoDeVentaCache = new PuntoDeVentaCache();
        puntoDeVentaCache.init();
        service = new CostosService(costosCache, puntoDeVentaCache);
    }

    @Test
    void agregarCostoCreatesSymmetricDirectPath() {
        service.agregarCosto(new CostoRequest(1, 10, 7));

        assertEquals(7, costosCache.getVecinos(1).get(10));
        assertEquals(7, costosCache.getVecinos(10).get(1));
    }

    @Test
    void agregarCostoWithMissingPuntoDeVentaFails() {
        var request = new CostoRequest(1, 999, 7);

        var exception = assertThrows(NotFoundException.class, () -> service.agregarCosto(request));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void agregarCostoWithNegativeCostFails() {
        var request = new CostoRequest(1, 2, -1);

        var exception = assertThrows(BadRequestException.class, () -> service.agregarCosto(request));

        assertEquals(Constants.COSTO_INVALID_EXCEPTION, exception.getMessage());
    }

    @Test
    void agregarCostoToSamePuntoDeVentaFails() {
        var request = new CostoRequest(1, 1, 1);

        var exception = assertThrows(BadRequestException.class, () -> service.agregarCosto(request));

        assertEquals(Constants.COSTO_SELF_LOOP_EXCEPTION, exception.getMessage());
    }

    @Test
    void removerCostoRemovesBothDirections() {
        service.removerCosto(1, 2);

        assertFalse(costosCache.getVecinos(1).containsKey(2));
        assertFalse(costosCache.getVecinos(2).containsKey(1));
    }

    @Test
    void removePuntoDeVentaRemovesNodeAndInboundEdges() {
        costosCache.removePuntoDeVenta(2);

        assertTrue(costosCache.getVecinos(2).isEmpty());
        assertFalse(costosCache.getVecinos(1).containsKey(2));
        assertFalse(costosCache.getVecinos(3).containsKey(2));
        assertFalse(costosCache.getVecinos(4).containsKey(2));
        assertFalse(costosCache.getVecinos(5).containsKey(2));
    }

    @Test
    void removerCostoWithMissingPuntoDeVentaFails() {
        var exception = assertThrows(NotFoundException.class, () -> service.removerCosto(1, 999));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void getVecinosReturnsDirectConnectionsWithNames() {
        var response = service.getVecinos(1);

        assertEquals("CABA", response.nombre());
        assertTrue(response.vecinoDto().stream().anyMatch(vecino -> vecino.id() == 2
                && vecino.nombre().equals("GBA_1")
                && vecino.costo() == 2));
    }

    @Test
    void getVecinosWithMissingPuntoDeVentaFails() {
        var exception = assertThrows(NotFoundException.class, () -> service.getVecinos(999));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void calcularCaminoMinimoToSamePuntoDeVentaReturnsZero() {
        var response = service.calcularCaminoMinimo(1, 1);

        assertEquals(0, response.costoTotal());
        assertEquals(java.util.List.of("CABA"), response.recorrido());
    }

    @Test
    void calcularCaminoMinimoUsesDirectPathWhenItIsMinimum() {
        var response = service.calcularCaminoMinimo(1, 4);

        assertEquals(11, response.costoTotal());
        assertEquals(java.util.List.of("CABA", "Santa Fe"), response.recorrido());
    }

    @Test
    void calcularCaminoMinimoUsesCheaperIndirectPathOverDirectPath() {
        service.agregarCosto(new CostoRequest(1, 5, 100));

        var response = service.calcularCaminoMinimo(1, 5);

        assertEquals(16, response.costoTotal());
        assertEquals(java.util.List.of("CABA", "GBA_1", "Córdoba"), response.recorrido());
    }

    @Test
    void calcularCaminoMinimoUsesCurrentPuntoDeVentaNames() {
        service.agregarCosto(new CostoRequest(1, 5, 100));
        puntoDeVentaCache.update(2, new PuntoDeVenta(2, "GBA Norte"));

        var response = service.calcularCaminoMinimo(1, 5);

        assertEquals(16, response.costoTotal());
        assertEquals(java.util.List.of("CABA", "GBA Norte", "Córdoba"), response.recorrido());
    }

    @Test
    void calcularCaminoMinimoWithMissingPuntoDeVentaFails() {
        var exception = assertThrows(NotFoundException.class, () -> service.calcularCaminoMinimo(1, 999));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void calcularCaminoMinimoWithoutPathFails() {
        puntoDeVentaCache.save(new PuntoDeVenta(11, "Mendoza"));

        var exception = assertThrows(NotFoundException.class, () -> service.calcularCaminoMinimo(1, 11));

        assertEquals(Constants.COSTOS_CAMINO_NOT_FOUND, exception.getMessage());
    }
}
