package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DetalleVentaResponseDTO {
    @JsonIgnore
    private Long id;
    private String nombreProd;
    private Integer cantProd;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private Long idProducto;

}
