package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Role;

public interface RoleRespository extends JpaRepository<Role, Long> {

}
