package com.oxxo.dto;

import com.oxxo.domain.PedidoCompra.Estado;
import java.time.LocalDate;

public record PedidoResponse(
  Long id, String proveedorId, String proveedorNombre,
  LocalDate fecha, LocalDate estimada, Estado estado, int items
){}
