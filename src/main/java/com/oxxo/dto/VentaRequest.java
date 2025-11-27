package com.oxxo.dto;

import java.util.List;
import lombok.Data;

/**
 * Request que recibe el backend desde el POS (venta.html)
 * medioPago = "Efectivo", "Tarjeta", etc.
 * items = lista de productos vendidos (sku + cantidad)
 */
@Data
public class VentaRequest {

    private String medioPago;
    private List<Item> items;

    @Data
    public static class Item {
        private String sku;
        private int cantidad;
    }
}
