package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.service.PuntoDeVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(Constants.PUNTO_DE_VENTA_ENDPOINT)
@RequiredArgsConstructor
public class PuntoDeVentaController {
    private final PuntoDeVentaService puntoDeVentaService;

    @GetMapping()
    public Collection<PuntoDeVenta> getAll() { return puntoDeVentaService.obtenerTodos(); }

    @GetMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<PuntoDeVenta> get(@PathVariable Integer id) {
        return ResponseEntity.ok(puntoDeVentaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PuntoDeVenta> create(@RequestBody PuntoDeVenta pdv) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(puntoDeVentaService.crear(pdv));
    }

    @PutMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<PuntoDeVenta> update(@PathVariable Integer id, @RequestBody PuntoDeVenta pdv) {
        return ResponseEntity.ok(puntoDeVentaService.actualizar(id, pdv));
    }

    @DeleteMapping(Constants.ID_PATH_VARIABLE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        puntoDeVentaService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
