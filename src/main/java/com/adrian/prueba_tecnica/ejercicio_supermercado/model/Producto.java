package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
 * Entidad que representa un producto en el catálogo del supermercado.
 * 
 * Esta clase modela la información de un producto disponible para la venta,
 * incluyendo su nombre, categoría, precio y stock. Los productos pueden ser
 * incluidos en detalles de venta y tienen un estado de actividad.
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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Producto {
    /**
     * Identificador único del producto.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto.
     * Identifica el producto de forma descriptiva.
     * Máximo 150 caracteres. No puede ser nulo.
     */
    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Categoría del producto.
     * Clasificación del producto en el catálogo (ej: Lácteos, Bebidas, Frutas).
     * Máximo 150 caracteres. No puede ser nulo.
     */
    @Column(nullable = false, length = 150)
    private String categoria;

    /**
     * Precio unitario del producto.
     * Valor numérico con precisión de hasta 10 dígitos y 2 decimales.
     * No puede ser nulo.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Cantidad disponible del producto en stock.
     * Número de unidades disponibles para la venta.
     * No puede ser nulo.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Indica si el producto está activo en el catálogo.
     * Un producto inactivo no puede ser vendido.
     */
    @Column(nullable = false)
    private boolean activo;

    /**
     * Fecha y hora en la que se creó el registro del producto.
     * Se asigna automáticamente mediante {@link #prePersist()} y no puede ser actualizada.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última actualización del registro del producto.
     * Se actualiza automáticamente mediante {@link #preUpdate()}.
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Método de ciclo de vida que se ejecuta antes de persistir la entidad.
     * Asigna la fecha y hora actual a {@code fechaCreacion} si no está establecida,
     * y actualiza {@code fechaActualizacion} con la fecha y hora actual.
     */
    @PrePersist
    protected void prePersist() {
        if (this.fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Método de ciclo de vida que se ejecuta antes de actualizar la entidad.
     * Actualiza {@code fechaActualizacion} con la fecha y hora actual.
     */
    @PreUpdate
    protected void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
