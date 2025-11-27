package com.oxxo.web;

import com.oxxo.domain.Inventario;
import com.oxxo.dto.AjusteRequest;
import com.oxxo.dto.InventarioItem;
import com.oxxo.dto.InventarioKpis;
import com.oxxo.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

  private final InventarioService service;

  public InventarioController(InventarioService service) {
    this.service = service;
  }

  // ⬇⬇⬇ CAMBIADO
  @PostMapping("/ajustes")
  public ResponseEntity<Void> ajustar(@RequestBody AjusteRequest req) {
    service.ajustar(req);      // hace el trabajo (resta/suma stock y guarda movimiento)
    return ResponseEntity.noContent().build(); // 204 sin cuerpo
  }

  @GetMapping("/{sku}")
  public Inventario porSku(@PathVariable String sku) {
    return service.porSku(sku);
  }

  @GetMapping
  public List<InventarioItem> listar() {
    return service.listarItems();
  }

  @GetMapping("/kpis")
  public InventarioKpis kpis() {
    return service.kpis();
  }
}