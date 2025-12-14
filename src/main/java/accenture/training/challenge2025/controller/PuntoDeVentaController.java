package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.cache.PuntoDeVentaCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(Constants.PUNTO_DE_VENTA_ENDPOINT)
@RequiredArgsConstructor
public class PuntoDeVentaController {
    private final PuntoDeVentaCache cache;

    @GetMapping()
    public Collection<PuntoDeVenta> getAll() { return cache.getAll(); }

    @GetMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<PuntoDeVenta> get(@PathVariable Integer id) {
        return cache.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PuntoDeVenta pdv) {
        cache.save(pdv);
        return ResponseEntity.ok().build();
    }

    @PutMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody PuntoDeVenta pdv) {
        cache.update(id, pdv);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cache.delete(id);
        return ResponseEntity.noContent().build();
    }
}
