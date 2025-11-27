package com.oxxo.dto;

public record ProveedoresKpis(
  long activos,
  double leadTimePromedioDias,
  long pedidosPendientes,
  double cumplimientoPorc          // recibidos / total * 100
) {}
