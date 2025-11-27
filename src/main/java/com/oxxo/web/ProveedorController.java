package com.oxxo.web;

import com.oxxo.domain.Proveedor;
import com.oxxo.dto.ProveedoresKpis;
import com.oxxo.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

  private final ProveedorService service;

  @GetMapping public List<Proveedor> listar(){ return service.listar(); }

  @GetMapping("/kpis")
  public ProveedoresKpis kpis(){ return service.kpis(); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Proveedor crear(@RequestBody @Valid Proveedor p){ return service.crear(p); }

  @PutMapping("/{id}")
  public Proveedor actualizar(@PathVariable("id") String id, @RequestBody @Valid Proveedor p){
    return service.actualizar(id, p);
  }

  @PatchMapping("/{id}/activo")
  public Proveedor activar(@PathVariable("id") String id, @RequestParam(name="value") boolean value){
    return service.activar(id, value);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable("id") String id){ service.eliminar(id); }
}