package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.cache.CostosCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.costos.CostoRequest;
import accenture.training.challenge2025.service.CostosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.COSTOS_ENDPOINT)
@RequiredArgsConstructor
public class CostosController {
    private final CostosCache costosCache;
    private final CostosService costosService;

    @PostMapping
    public ResponseEntity<?> addCosto(@RequestBody CostoRequest req) {
        costosCache.addCosto(req.origen(), req.destino(), req.costo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Constants.ORIGEN_DESTINO_PATH_VARIABLE)
    public ResponseEntity<?> deleteCosto(@PathVariable int origen, @PathVariable int destino) {
        costosCache.removeCosto(origen, destino);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Constants.ORIGEN_PATH_VARIABLE)
    public ResponseEntity<?> vecinos(@PathVariable int origen) {
        return ResponseEntity.ok(costosService.getVecinos(origen));
    }

    @GetMapping(Constants.COSTOS_CAMINO_ENDPOINT)
    public ResponseEntity<?> camino(@PathVariable int origen, @PathVariable int destino) {
        var result = costosService.calcularCaminoMinimo(origen, destino);
        return ResponseEntity.ok(result);
    }
}
