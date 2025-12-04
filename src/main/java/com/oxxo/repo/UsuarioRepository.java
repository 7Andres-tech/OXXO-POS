package com.oxxo.repo;

import com.oxxo.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findFirstByEmailIgnoreCaseOrderByIdAsc(String email);

    Optional<Usuario> findFirstByUsernameIgnoreCaseOrderByIdAsc(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);
}
