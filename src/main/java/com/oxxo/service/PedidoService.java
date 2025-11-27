package com.oxxo.service;

import com.oxxo.domain.*;
import com.oxxo.dto.AjusteRequest;
import com.oxxo.dto.PedidoRequest;
import com.oxxo.dto.PedidoResponse;
import com.oxxo.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

  private final PedidoCompraRepository pedidoRepo;
  private final PedidoItemRepository itemRepo;
  private final ProveedorRepository proveedorRepo;
  private final ProductoRepository productoRepo;
  private final InventarioService inventarioService;

  /** Listado como DTO para evitar problemas de carga perezosa al serializar. */
  @Transactional(readOnly = true)
  public List<PedidoResponse> listar() {
    return pedidoRepo.findAllWithProveedor()  // trae proveedor con join fetch
        .stream()
        .map(this::toResp)
        .toList();
  }

  @Transactional
  public PedidoResponse crear(PedidoRequest req) {
    var proveedor = proveedorRepo.findById(req.getProveedorId()).orElseThrow();

    var pedido = new PedidoCompra();
    pedido.setProveedor(proveedor);
    pedido.setEstimada(req.getEstimada());
    pedido = pedidoRepo.save(pedido);

    for (var it : req.getItems()) {
      var prod = productoRepo.findById(it.getSku()).orElseThrow();
      var pi = new PedidoItem();
      pi.setPedido(pedido);
      pi.setProducto(prod);
      pi.setCantidad(it.getCantidad());
      itemRepo.save(pi);
      pedido.getItems().add(pi);
    }
    return toResp(pedido);
  }

  @Transactional
  public PedidoResponse recibir(Long id) {
    var pedido = pedidoRepo.findById(id).orElseThrow();
    if (pedido.getEstado() != PedidoCompra.Estado.PENDIENTE) return toResp(pedido);

    // Ingresar inventario por cada ítem
    for (var it : pedido.getItems()) {
      var ar = AjusteRequest.builder()
          .sku(it.getProducto().getSku())
          .tipo(MovimientoInventario.Tipo.INGRESO)
          .cantidad(it.getCantidad())
          .motivo("Recepción pedido PO-" + id)
          .build();
      inventarioService.ajustar(ar);
    }
    pedido.setEstado(PedidoCompra.Estado.RECIBIDO);
    return toResp(pedido);
  }

  @Transactional
  public PedidoResponse cancelar(Long id) {
    var pedido = pedidoRepo.findById(id).orElseThrow();
    if (pedido.getEstado() == PedidoCompra.Estado.PENDIENTE) {
      pedido.setEstado(PedidoCompra.Estado.CANCELADO);
    }
    return toResp(pedido);
  }

  /** Mapea entidad -> DTO ligero para la API. */
  private PedidoResponse toResp(PedidoCompra p) {
    return new PedidoResponse(
        p.getId(),
        p.getProveedor().getId(),
        p.getProveedor().getNombre(),
        p.getFecha(),
        p.getEstimada(),
        p.getEstado(),
        p.getItems() == null ? 0 : p.getItems().size()
    );
  }
}
