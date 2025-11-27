package com.oxxo.repo;

import com.oxxo.domain.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProveedorRepository extends JpaRepository<Proveedor, String> {

  long countByActivoTrue();

  @Query("select coalesce(avg(p.leadTimeDias),0) from Proveedor p")
  Double leadTimePromedioDias();
}
