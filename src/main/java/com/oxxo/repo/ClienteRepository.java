package com.oxxo.repo;

import com.oxxo.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

  long countByComprasGreaterThan(int n);

  @Query("select coalesce(sum(c.puntos),0) from Cliente c")
  long sumPuntos();
}
