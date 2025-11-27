package com.oxxo.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.oxxo.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailIgnoreCase(String email);
}
