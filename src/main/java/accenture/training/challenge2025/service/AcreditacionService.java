package accenture.training.challenge2025.service;

import accenture.training.challenge2025.cache.PuntoDeVentaCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionResponse;
import accenture.training.challenge2025.entity.Acreditacion;
import accenture.training.challenge2025.exception.NotFoundException;
import accenture.training.challenge2025.repository.AcreditacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcreditacionService {
    private final PuntoDeVentaCache pdvCache;
    private final AcreditacionRepository acredRepo;

    public AcreditacionResponse crearAcreditacion(AcreditacionesRequest acreditacionRequest) {
        var pdv = pdvCache
                    .findById(acreditacionRequest.puntoVentaId())
                    .orElseThrow(() -> new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));

        var newAcreditacion = Acreditacion
                                .builder()
                                .importe(acreditacionRequest.importe())
                                .puntoVentaId(acreditacionRequest.puntoVentaId())
                                .nombrePuntoVenta(pdv.nombre())
                                .fechaRecepcion(LocalDateTime.now())
                                .build();

        var savedAcreditacion = acredRepo.save(newAcreditacion);

        return new AcreditacionResponse(
            savedAcreditacion.getId(),
            savedAcreditacion.getImporte(),
            savedAcreditacion.getPuntoVentaId(),
            savedAcreditacion.getNombrePuntoVenta(),
            savedAcreditacion.getFechaRecepcion()
        );
    }

    public List<AcreditacionResponse> obtenerAcreditaciones() {
        return acredRepo
                 .findAll()
                 .stream()
                 .map(ac -> new AcreditacionResponse(
                    ac.getId(),
                    ac.getImporte(),
                    ac.getPuntoVentaId(),
                    ac.getNombrePuntoVenta(),
                    ac.getFechaRecepcion()
                 ))
                 .toList();
    }
}
