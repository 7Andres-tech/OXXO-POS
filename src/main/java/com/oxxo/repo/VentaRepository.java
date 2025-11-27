package com.oxxo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oxxo.domain.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Para "Tickets recientes" en el POS
    List<Venta> findTop20ByOrderByFechaDesc();
}
