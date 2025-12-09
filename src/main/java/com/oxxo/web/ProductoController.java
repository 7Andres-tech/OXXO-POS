package com.oxxo.web;

import com.oxxo.dto.ProductoRequest;
import com.oxxo.dto.ProductoResponse;
import com.oxxo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return service.listar(); // devuelve todos los productos
    }

    @PostMapping
    public ProductoResponse crear(@RequestBody @Valid ProductoRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{sku}")
    public ProductoResponse actualizar(@PathVariable String sku,
                                       @RequestBody @Valid ProductoRequest req) {
        return service.actualizar(sku, req);
    }

    @PutMapping("/{sku}/activo")
public void actualizarEstado(@PathVariable String sku,
                             @RequestBody Map<String, Boolean> body) {
    boolean activo = body.getOrDefault("activo", true);
    service.actualizarEstado(sku, activo);
}

@DeleteMapping("/{sku}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void eliminar(@PathVariable String sku) {
    service.eliminar(sku);
}

}
