package accenture.training.challenge2025.service;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.repository.PuntoDeVentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuntoDeVentaServiceTests {
    @Mock
    private PuntoDeVentaRepository repository;

    @InjectMocks
    private PuntoDeVentaService service;

    @Test
    void obtenerTodosReturnsRepositoryValues() {
        var puntos = List.of(new PuntoDeVenta(1, "CABA"));
        when(repository.getAll()).thenReturn(puntos);

        assertEquals(puntos, service.obtenerTodos());
    }

    @Test
    void crearValidPuntoDeVentaSavesIt() {
        var pdv = new PuntoDeVenta(11, "Mendoza");
        when(repository.findById(pdv.id())).thenReturn(Optional.empty());

        assertEquals(pdv, service.crear(pdv));

        verify(repository).save(pdv);
    }

    @Test
    void crearDuplicatedPuntoDeVentaFails() {
        var pdv = new PuntoDeVenta(1, "CABA");
        when(repository.findById(pdv.id())).thenReturn(Optional.of(pdv));

        var exception = assertThrows(BadRequestException.class, () -> service.crear(pdv));

        assertEquals(Constants.PUNTO_DE_VENTA_DUPLICATED_EXCEPTION, exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void crearInvalidPuntoDeVentaFails() {
        var pdv = new PuntoDeVenta(null, " ");

        var exception = assertThrows(BadRequestException.class, () -> service.crear(pdv));

        assertEquals(Constants.PUNTO_DE_VENTA_INVALID_EXCEPTION, exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarExistingPuntoDeVentaReplacesIt() {
        var pdv = new PuntoDeVenta(11, "Mendoza Centro");
        when(repository.findById(pdv.id())).thenReturn(Optional.of(new PuntoDeVenta(11, "Mendoza")));

        assertEquals(pdv, service.actualizar(pdv.id(), pdv));

        verify(repository).update(pdv.id(), pdv);
    }

    @Test
    void actualizarWithMismatchedIdFails() {
        var pdv = new PuntoDeVenta(12, "Mendoza");

        var exception = assertThrows(BadRequestException.class, () -> service.actualizar(11, pdv));

        assertEquals(Constants.PUNTO_DE_VENTA_ID_MISMATCH_EXCEPTION, exception.getMessage());
        verify(repository, never()).update(any(), any());
    }

    @Test
    void actualizarMissingPuntoDeVentaFails() {
        var pdv = new PuntoDeVenta(11, "Mendoza");
        when(repository.findById(pdv.id())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> service.actualizar(pdv.id(), pdv));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
        verify(repository, never()).update(any(), any());
    }

    @Test
    void borrarExistingPuntoDeVentaDeletesIt() {
        var pdv = new PuntoDeVenta(11, "Mendoza");
        when(repository.findById(pdv.id())).thenReturn(Optional.of(pdv));

        service.borrar(pdv.id());

        verify(repository).delete(pdv.id());
    }

    @Test
    void borrarMissingPuntoDeVentaFails() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> service.borrar(999));

        assertEquals(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION, exception.getMessage());
        verify(repository, never()).delete(any());
    }
}
