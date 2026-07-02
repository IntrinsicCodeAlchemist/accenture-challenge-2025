package accenture.training.challenge2025.controller;

import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.acreditaciones.AcreditacionesRequest;
import accenture.training.challenge2025.service.AcreditacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.ACREDITACIONES_ENDPOINT)
@RequiredArgsConstructor
public class AcreditacionController {
    private final AcreditacionService acreditacionService;

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody AcreditacionesRequest acreditacionRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(acreditacionService.crearAcreditacion(acreditacionRequest));
    }

    @GetMapping
    public ResponseEntity<?> obtener() {
        return ResponseEntity.ok(acreditacionService.obtenerAcreditaciones());
    }
}
