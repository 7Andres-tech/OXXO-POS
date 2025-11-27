package com.oxxo.web;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oxxo.dto.VentaRequest;
import com.oxxo.dto.VentaResponse;
import com.oxxo.service.VentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081") // o http://localhost:4200 según desde dónde llames
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public VentaResponse registrar(@RequestBody VentaRequest request) {
        return ventaService.registrarVenta(request);
    }

    @GetMapping
    public List<VentaResponse> listarUltimas() {
        return ventaService.ultimasVentas();
    }
}
