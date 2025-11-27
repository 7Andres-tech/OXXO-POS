package com.oxxo.repo;

import com.oxxo.domain.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Long> { }
