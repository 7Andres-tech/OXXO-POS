package com.oxxo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oxxo.domain.MovimientoInventario;
import com.oxxo.domain.Producto;
import com.oxxo.domain.Venta;
import com.oxxo.dto.AjusteRequest;
import com.oxxo.dto.VentaRequest;
import com.oxxo.dto.VentaResponse;
import com.oxxo.repo.ProductoRepository;
import com.oxxo.repo.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepo;
    private final ProductoRepository productoRepo;
    private final InventarioService inventarioService;

    /**
     * Registra una venta:
     * - Calcula el total usando el precio del producto.
     * - Ajusta el inventario (tipo VENTA) por cada ítem.
     * - Guarda la venta en BD.
     * - Devuelve un DTO con datos básicos + código de ticket.
     */
    @Transactional
    public VentaResponse registrarVenta(VentaRequest req) {

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta no tiene ítems.");
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setMedioPago(req.getMedioPago());

        BigDecimal total = BigDecimal.ZERO;

        // 1. Procesar ítems
        for (VentaRequest.Item it : req.getItems()) {

            // Buscar producto por SKU
            Producto p = productoRepo.findById(it.getSku())
                    .orElseThrow(() ->
                            new IllegalArgumentException("SKU no encontrado: " + it.getSku()));

            // Precio y subtotal
            BigDecimal precio = p.getPrecio(); // ajusta si tu campo se llama distinto
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(it.getCantidad()));

            total = total.add(subtotal);

            // Ajustar inventario usando el servicio que ya tienes
            AjusteRequest ar = AjusteRequest.builder()
                    .sku(p.getSku())
                    .tipo(MovimientoInventario.Tipo.VENTA)
                    .cantidad(it.getCantidad())
                    .motivo("Venta")
                    .build();

            inventarioService.ajustar(ar);
        }

        // 2. Guardar venta
        venta.setTotal(total);
        venta = ventaRepo.save(venta);

        // 3. Generar código de ticket SOLO en la respuesta (no se guarda en BD)
        String ticketCode = buildTicketCode(venta.getId());

        return new VentaResponse(
                venta.getId(),
                ticketCode,
                venta.getFecha(),
                venta.getMedioPago(),
                venta.getTotal()
        );
    }

    /**
     * Devuelve las últimas 20 ventas para el widget de "Tickets recientes".
     */
    @Transactional(readOnly = true)
    public List<VentaResponse> ultimasVentas() {
        return ventaRepo.findTop20ByOrderByFechaDesc()
                .stream()
                .map(v -> new VentaResponse(
                        v.getId(),
                        buildTicketCode(v.getId()),
                        v.getFecha(),
                        v.getMedioPago(),
                        v.getTotal()
                ))
                .toList();
    }

    // ----------------- Helpers -----------------

    private String buildTicketCode(Long id) {
        if (id == null) return "TCK-0000";
        return "TCK-" + String.format("%04d", id);
    }
}
