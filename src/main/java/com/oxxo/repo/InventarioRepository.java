package com.oxxo.repo;

import com.oxxo.domain.Inventario;
import com.oxxo.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProducto(Producto producto);

    @Query("""
           select i from Inventario i
           join fetch i.producto p
           left join fetch p.proveedor
           """)
    List<Inventario> findAllWithProductoProveedor();

    // KPIs
    @Query("select count(distinct i.producto) from Inventario i where i.producto.activo = true")
    long countDistinctByProducto_ActivoTrue();

    @Query("select coalesce(sum(i.stock),0) from Inventario i")
    int sumStock();

    @Query("select count(i) from Inventario i where i.stock > 0 and i.stock < i.minimo")
    int countStockBajo();

    @Query("select count(i) from Inventario i where i.stock = 0")
    int countSinStock();
}
