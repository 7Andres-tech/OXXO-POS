package com.oxxo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta simple para la venta registrada
 * y para listar las últimas ventas (tickets recientes).
 */
@Data
@AllArgsConstructor
public class VentaResponse {

    private Long id;
    private String ticket;          // ej. "TCK-0001"
    private LocalDateTime fecha;
    private String medioPago;
    private BigDecimal total;
}
