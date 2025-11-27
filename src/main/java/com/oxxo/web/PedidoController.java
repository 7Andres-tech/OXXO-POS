package com.oxxo.web;

import com.oxxo.dto.PedidoRequest;
import com.oxxo.dto.PedidoResponse;
import com.oxxo.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

  private final PedidoService service;

  @GetMapping
  public List<PedidoResponse> listar() {   // ahora devuelve DTO
    return service.listar();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PedidoResponse crear(@RequestBody @Valid PedidoRequest req) {
    return service.crear(req);
  }

  @PostMapping("/{id}/recibir")
  public PedidoResponse recibir(@PathVariable("id") Long id) {
    return service.recibir(id);
  }

  @PostMapping("/{id}/cancelar")
  public PedidoResponse cancelar(@PathVariable("id") Long id) {
    return service.cancelar(id);
  }
}
