package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

}
