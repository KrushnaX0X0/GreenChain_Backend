package com.krish.AgariBackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.krish.AgariBackend.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM users_roles WHERE role_name = :roleName", nativeQuery = true)
    long countByRoleName(@org.springframework.data.repository.query.Param("roleName") String roleName);
}
