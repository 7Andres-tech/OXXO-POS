package com.oxxo.dto;

public record InventarioKpis(
  long skusActivos, long stockTotal, long stockBajo, long sinStock
) {}