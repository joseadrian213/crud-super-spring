package com.adrian.prueba_tecnica.ejercicio_supermercado.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DetalleVentaDTO {
    private Long id;
    @NotNull
    @NotBlank
    private String nombreProd;
    @NotNull
    @Positive
    private Integer cantProd;
    @NotNull
    @Positive
    private BigDecimal precio;
    @Null
    @Positive
    private BigDecimal subtotal;
    @NotNull
    private Long idProducto;

}
