package com.adrian.prueba_tecnica.ejercicio_supermercado.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
        @Query("SELECT v FROM Sale v " +
                        "JOIN FETCH v.branch s " +
                        "JOIN FETCH v.detail d " +
                        "JOIN FETCH d.product p " +
                        "WHERE v.id = :id")
        Optional<Sale> findSaleWithDetailAndProduct(@Param("id") Long id);

        @Query("SELECT DISTINCT v FROM Sale v " +
                        "JOIN FETCH v.branch " +
                        "JOIN FETCH v.detail d " +
                        "JOIN FETCH d.product")
        List<Sale> findAllWithDetailAndProduct();

        @Query("SELECT DISTINCT v FROM Sale v " +
                        "JOIN FETCH v.branch s " +
                        "JOIN FETCH v.detail d " +
                        "JOIN FETCH d.product p " +
                        "WHERE s.id = :id")
        List<Sale> findByBranchIdWithDetails(@Param("id") Long id);

        @Query("SELECT DISTINCT v FROM Sale v " +
                        "JOIN FETCH v.branch s " +
                        "JOIN FETCH v.detail d " +
                        "JOIN FETCH d.product p " +
                        "WHERE s.id = :branchId AND v.date BETWEEN :start AND :end")
        List<Sale> findByBranchIdAndDateBetweenWithDetails(
                        @Param("branchId") Long branchId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

}
