package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una sucursal del supermercado.
 * 
 * Esta clase modela la información de una sucursal, incluyendo su nombre,
 * ubicación física y estado de actividad. Cada sucursal puede registrar múltiples ventas.
 * 
 * @author Adrian
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Branch {
    /**
     * Identificador único de la sucursal.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la sucursal.
     * Identifica la sucursal de forma descriptiva.
     * Máximo 150 caracteres. No puede ser nulo.
     */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Dirección física de la sucursal.
     * Ubicación completa donde se encuentra la sucursal.
     * Máximo 200 caracteres. No puede ser nulo.
     */
    @Column(nullable = false, length = 200)
    private String address;

    /**
     * Indica si la sucursal está activa en el sistema.
     * Una sucursal inactiva no puede registrar nuevas ventas.
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * Fecha y hora en la que se creó el registro de la sucursal.
     * Se asigna automáticamente mediante {@link #prePersist()} y no puede ser actualizada.
     */
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    /**
     * Fecha y hora de la última actualización del registro de la sucursal.
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
