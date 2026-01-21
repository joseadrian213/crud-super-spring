package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa el detalle de un producto dentro de una venta.
 * 
 * Esta clase modela la información de cada producto vendido en una venta,
 * incluyendo la cantidad, precio unitario y subtotal. Un detalle de venta
 * establece la relación entre una venta y los productos vendidos.
 * 
 * @author Adrian
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    /**
     * Identificador único del detalle de venta.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Venta a la que pertenece este detalle.
     * Relación muchos-a-uno: múltiples detalles pertenecen a una venta.
     * Este campo es obligatorio y no puede ser nulo.
     * 
     * @see Venta
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    private Venta venta;

    /**
     * Producto que fue vendido en este detalle.
     * Relación muchos-a-uno: múltiples detalles pueden referirse al mismo producto.
     * Este campo es obligatorio y no puede ser nulo.
     * 
     * @see Producto
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Cantidad de unidades vendidas del producto.
     * Número entero que representa cuántas unidades se vendieron.
     * No puede ser nulo.
     */
    @Column(name = "cantidad_producto", nullable = false)
    private Integer cantProd;

    /**
     * Precio unitario del producto en el momento de la venta.
     * Valor numérico con precisión de hasta 10 dígitos y 2 decimales.
     * No puede ser nulo.
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    /**
     * Subtotal de esta línea de venta (cantidad × precio unitario).
     * Valor numérico con precisión de hasta 10 dígitos y 2 decimales.
     * No puede ser nulo.
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

}
