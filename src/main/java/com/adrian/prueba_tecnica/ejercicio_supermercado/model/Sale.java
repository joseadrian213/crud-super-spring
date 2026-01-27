package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.enums.EstadoVentaEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una venta realizada en el supermercado.
 * 
 * Esta clase modela la información general de una venta, incluyendo su fecha,
 * estado, total y la sucursal donde se realizó. Cada venta puede contener
 * múltiples detalles de venta (productos vendidos).
 * 
 * @author Adrian
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Sale {
    /**
     * Identificador único de la venta.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en la que se realizó la venta.
     * No puede ser nula.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Estado actual de la venta.
     * Puede ser: PENDIENTE, COMPLETADA, CANCELADA u otro estado definido en {@link EstadoVentaEnum}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoVentaEnum status;

    /**
     * Monto total de la venta.
     * Valor numérico con precisión de hasta 12 dígitos y 2 decimales.
     * No puede ser nulo.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    /**
     * Sucursal donde se realizó la venta.
     * Relación muchas-a-una: múltiples ventas pueden pertenecer a una sucursal.
     * Este campo es obligatorio y no puede ser nulo.
     * 
     * @see Branch
     */
    // Muchas ventas asociadas a una sucursal
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /**
     * Lista de detalles de la venta.
     * Cada detalle representa un producto vendido con su cantidad y precio.
     * La relación es uno-a-muchos: una venta puede tener múltiples detalles.
     * Los detalles se eliminan en cascada cuando se elimina la venta.
     * 
     * @see SaleDetail
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference
    private List<SaleDetail> detail = new ArrayList<>();

    /**
     * Fecha y hora en la que se creó el registro de la venta.
     * Se asigna automáticamente mediante {@link #prePersist()} y no puede ser actualizada.
     */
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    /**
     * Fecha y hora de la última actualización del registro de la venta.
     * Se actualiza automáticamente mediante {@link #preUpdate()}.
     */
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    /**
     * Método de ciclo de vida que se ejecuta antes de persistir la entidad.
     * Asigna la fecha y hora actual a {@code creationDate} si no está establecida,
     * y actualiza {@code updateDate} con la fecha y hora actual.
     */
    @PrePersist
    protected void prePersist() {
        if (this.creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        updateDate = LocalDateTime.now();
    }

    /**
     * Método de ciclo de vida que se ejecuta antes de actualizar la entidad.
     * Actualiza {@code updateDate} con la fecha y hora actual.
     */
    @PreUpdate
    protected void preUpdate() {
        updateDate = LocalDateTime.now();
    }

}
