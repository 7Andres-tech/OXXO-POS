package com.oxxo.repo;

import com.oxxo.domain.PedidoCompra;
import com.oxxo.domain.PedidoCompra.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {

  List<PedidoCompra> findAllByOrderByFechaDesc();

  long countByEstado(Estado estado);

  @Query("select count(p) from PedidoCompra p")
  long totalPedidos();

  @Query("select p from PedidoCompra p join fetch p.proveedor order by p.fecha desc")
  List<PedidoCompra> findAllWithProveedor();
}
