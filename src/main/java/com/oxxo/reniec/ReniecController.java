package com.oxxo.reniec;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reniec")
@RequiredArgsConstructor
public class ReniecController {

    private final ReniecService reniecService;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ReniecDto> porDni(@PathVariable String dni) {
        ReniecDto dto = reniecService.consultarDni(dni);
        if (dto == null) {
            return ResponseEntity.notFound().build();   // 404 si no hay datos o error externo
        }
        return ResponseEntity.ok(dto);                 // 200 con JSON
    }
}
