package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.service.AcreditacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.ACREDITACIONES_ENDPOINT)
@RequiredArgsConstructor
public class AcreditacionController {
    private final AcreditacionService acreditacionService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AcreditacionesRequest acreditacionRequest) {
        return ResponseEntity.ok(acreditacionService.crearAcreditacion(acreditacionRequest));
    }

    @GetMapping
    public ResponseEntity<?> obtener() {
        return ResponseEntity.ok(acreditacionService.obtenerAcreditaciones());
    }
}
