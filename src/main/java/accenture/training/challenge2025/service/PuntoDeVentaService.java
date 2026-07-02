package accenture.training.challenge2025.service;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.repository.PuntoDeVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PuntoDeVentaService {
    private final PuntoDeVentaRepository repository;

    public Collection<PuntoDeVenta> obtenerTodos() { return repository.getAll(); }

    public PuntoDeVenta obtenerPorId(Integer id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
    }

    public PuntoDeVenta crear(PuntoDeVenta pdv) {
        validarPuntoDeVenta(pdv);
        repository
                .findById(pdv.id())
                .ifPresent(existing -> { throw new BadRequestException(Constants.PUNTO_DE_VENTA_DUPLICATED_EXCEPTION); });

        repository.save(pdv);
        return pdv;
    }

    public PuntoDeVenta actualizar(Integer id, PuntoDeVenta pdv) {
        validarPuntoDeVenta(pdv);
        validarIdPath(id);

        if (!id.equals(pdv.id())) {
            throw new BadRequestException(Constants.PUNTO_DE_VENTA_ID_MISMATCH_EXCEPTION);
        }

        obtenerPorId(id);
        repository.update(id, pdv);
        return pdv;
    }

    public void borrar(Integer id) {
        validarIdPath(id);
        obtenerPorId(id);
        repository.delete(id);
    }

    private void validarIdPath(Integer id) {
        if (id == null) {
            throw new BadRequestException(Constants.PUNTO_DE_VENTA_INVALID_EXCEPTION);
        }
    }

    private void validarPuntoDeVenta(PuntoDeVenta pdv) {
        if (pdv == null || pdv.id() == null || pdv.nombre() == null || pdv.nombre().isBlank()) {
            throw new BadRequestException(Constants.PUNTO_DE_VENTA_INVALID_EXCEPTION);
        }
    }
}
