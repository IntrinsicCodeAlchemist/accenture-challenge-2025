package accenture.training.challenge2025.cache;

import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.repository.PuntoDeVentaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PuntoDeVentaCache implements PuntoDeVentaRepository {
    private final Map<Integer, PuntoDeVenta> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        cache.put(1, new PuntoDeVenta(1, "CABA"));
        cache.put(2, new PuntoDeVenta(2, "GBA_1"));
        cache.put(3, new PuntoDeVenta(3, "GBA_2"));
        cache.put(4, new PuntoDeVenta(4, "Santa Fe"));
        cache.put(5, new PuntoDeVenta(5, "Córdoba"));
        cache.put(6, new PuntoDeVenta(6, "Misiones"));
        cache.put(7, new PuntoDeVenta(7, "Salta"));
        cache.put(8, new PuntoDeVenta(8, "Chubut"));
        cache.put(9, new PuntoDeVenta(9, "Santa Cruz"));
        cache.put(10, new PuntoDeVenta(10, "Catamarca"));
    }

    public Collection<PuntoDeVenta> getAll() { return cache.values(); }

    public Optional<PuntoDeVenta> findById(Integer id) { return Optional.ofNullable(cache.get(id)); }

    public void save(PuntoDeVenta pdv) { cache.put(pdv.id(), pdv); }

    public void update(Integer id, PuntoDeVenta pdv) { cache.replace(id, pdv); }

    public void delete(Integer id) { cache.remove(id); }
}
