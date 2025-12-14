package accenture.training.challenge2025.repository;

import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;

import java.util.Collection;
import java.util.Optional;

public interface PuntoDeVentaRepository {
    Collection<PuntoDeVenta> getAll();
    Optional<PuntoDeVenta> findById(Integer id);
    void save(PuntoDeVenta pdv);
    void update(Integer id, PuntoDeVenta pdv);
    void delete(Integer id);
}
