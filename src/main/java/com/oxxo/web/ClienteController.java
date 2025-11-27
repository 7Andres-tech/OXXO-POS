package com.oxxo.web;

import com.oxxo.domain.Cliente;
import com.oxxo.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

  private final ClienteService service;

  @GetMapping public List<Cliente> listar() { return service.listar(); }

  @GetMapping("/kpis")
  public ClienteService.ClientesKpis kpis() { return service.kpis(); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Cliente crear(@RequestBody @Valid Cliente c) { return service.crear(c); }

  @PutMapping("/{doc}")
  public Cliente actualizar(@PathVariable String doc, @RequestBody @Valid Cliente c) {
    return service.actualizar(doc, c);
  }

  @DeleteMapping("/{doc}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable String doc) { service.eliminar(doc); }

  // Marca una compra y suma puntos
  @PostMapping("/{doc}/compra")
  public Cliente compra(@PathVariable String doc, @RequestParam(defaultValue = "0") int puntos) {
    return service.registrarCompra(doc, puntos);
  }
}
