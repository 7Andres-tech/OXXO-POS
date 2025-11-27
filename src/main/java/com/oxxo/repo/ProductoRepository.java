package com.oxxo.repo;

import com.oxxo.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {

  long countByActivoTrue();

  @Query("select count(distinct p.categoria) from Producto p")
  long countCategorias();

  @Query("select coalesce(avg(p.precio),0) from Producto p")
  BigDecimal avgPrecio();

  @Query("select p from Producto p left join fetch p.proveedor")
  List<Producto> findAllWithProveedor();
}
