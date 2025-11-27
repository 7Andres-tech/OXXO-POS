package com.oxxo.service;

import com.oxxo.domain.Inventario;
import com.oxxo.domain.MovimientoInventario;
import com.oxxo.domain.Producto;
import com.oxxo.dto.AjusteRequest;
import com.oxxo.dto.InventarioItem;
import com.oxxo.dto.InventarioKpis;
import com.oxxo.repo.InventarioRepository;
import com.oxxo.repo.MovimientoRepository;
import com.oxxo.repo.ProductoRepository;
import com.oxxo.web.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepo;
    private final MovimientoRepository movimientoRepo;
    private final ProductoRepository productoRepo;

    // --------- MÉTODO CLAVE: AJUSTAR (venta, pedido, ajuste rápido) ---------
    @Transactional
    public Inventario ajustar(AjusteRequest req) {

        // 1. Buscar producto por SKU
        Producto prod = productoRepo.findById(req.sku())
                .orElseThrow(() -> new NotFoundException("SKU no encontrado: " + req.sku()));

        // 2. Buscar inventario; si no hay, crear
        Inventario inv = inventarioRepo.findByProducto(prod)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setProducto(prod);
                    nuevo.setStock(0);    // stock inicial 0
                    nuevo.setMinimo(0);   // mínimo 0 por defecto
                    return nuevo;
                });

        // 3. Aplicar ajuste
        int delta = req.tipo() == MovimientoInventario.Tipo.INGRESO
                ? req.cantidad()
                : -req.cantidad();

        int stockActual = inv.getStock() == null ? 0 : inv.getStock();
        int nuevoStock = stockActual + delta;
        if (nuevoStock < 0) {
            nuevoStock = 0;
        }
        inv.setStock(nuevoStock);

        // 4. Guardar inventario actualizado
        Inventario guardado = inventarioRepo.save(inv);

        // 5. Registrar movimiento
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(prod);
        mov.setFecha(LocalDateTime.now());
        mov.setTipo(req.tipo());
        mov.setCantidad(req.cantidad());
        mov.setMotivo(req.motivo());
        movimientoRepo.save(mov);

        return guardado;
    }

    // --------- LISTAR COMO DTO (para inventario.html) ---------
    @Transactional(readOnly = true)
    public List<InventarioItem> listarItems() {
        List<Inventario> lista = inventarioRepo.findAllWithProductoProveedor();

        return lista.stream()
                .map(i -> new InventarioItem(
                        i.getProducto().getSku(),
                        i.getProducto().getNombre(),
                        i.getProducto().getCategoria(),
                        i.getProducto().getProveedor() == null
                                ? null
                                : i.getProducto().getProveedor().getNombre(),
                        i.getStock(),
                        i.getMinimo()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Inventario porSku(String sku) {
        Producto prod = productoRepo.findById(sku)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + sku));

        return inventarioRepo.findByProducto(prod)
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para SKU: " + sku));
    }

    @Transactional(readOnly = true)
    public InventarioKpis kpis() {
        List<Inventario> lista = inventarioRepo.findAllWithProductoProveedor();

        int skusActivos = (int) lista.stream()
                .filter(i -> i.getStock() != null && i.getStock() > 0)
                .count();

        int stockTotal = lista.stream()
                .mapToInt(i -> i.getStock() == null ? 0 : i.getStock())
                .sum();

        int stockBajo = (int) lista.stream()
                .filter(i -> {
                    int stock = i.getStock() == null ? 0 : i.getStock();
                    int min = i.getMinimo() == null ? 0 : i.getMinimo();
                    return min > 0 && stock > 0 && stock < min;
                })
                .count();

        int sinStock = (int) lista.stream()
                .filter(i -> i.getStock() == null || i.getStock() == 0)
                .count();

        return new InventarioKpis(skusActivos, stockTotal, stockBajo, sinStock);
    }
}
