package com.oxxo.dto;

public record InventarioItem(
        String sku,
        String nombre,
        String categoria,
        String proveedor,
        Integer stock,
        Integer minimo
) {}
