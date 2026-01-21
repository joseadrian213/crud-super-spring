package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
        @Query("SELECT v FROM Venta v " +
                        "JOIN FETCH v.sucursal s " +
                        "JOIN FETCH v.detalle d " +
                        "JOIN FETCH d.producto p " +
                        "WHERE v.id = :id")
        Optional<Venta> findVentaConDetalleYProducto(@Param("id") Long id);

        @Query("SELECT DISTINCT v FROM Venta v " +
                        "JOIN FETCH v.sucursal " +
                        "JOIN FETCH v.detalle d " +
                        "JOIN FETCH d.producto")
        List<Venta> findAllConDetalleYProducto();

        @Query("SELECT DISTINCT v FROM Venta v " +
                        "JOIN FETCH v.sucursal s " +
                        "JOIN FETCH v.detalle d " +
                        "JOIN FETCH d.producto p " +
                        "WHERE s.id = :id")
        List<Venta> findBySucursalIdConDetalles(@Param("id") Long id);

        @Query("SELECT DISTINCT v FROM Venta v " +
                        "JOIN FETCH v.sucursal s " +
                        "JOIN FETCH v.detalle d " +
                        "JOIN FETCH d.producto p " +
                        "WHERE s.id = :sucursalId AND v.fecha BETWEEN :inicio AND :fin")
        List<Venta> findBySucursalIdAndFechaBetweenConDetalles(
                        @Param("sucursalId") Long sucursalId,
                        @Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin);

}
