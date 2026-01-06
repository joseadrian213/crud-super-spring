package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

}
