package com.oxxo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequest(
  @NotBlank String sku,
  @NotBlank String nombre,
  String codigoBarras,
  String categoria,
  String proveedorId,          // id del proveedor (PV001…)
  @NotNull @DecimalMin("0.00") BigDecimal precio,
  @NotNull @DecimalMin("0.00") BigDecimal costo,
  boolean activo,
  Integer stockMinimo
) {}
