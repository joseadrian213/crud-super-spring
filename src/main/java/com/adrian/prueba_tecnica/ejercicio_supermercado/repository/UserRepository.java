package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWhitRoles(String username);
}
