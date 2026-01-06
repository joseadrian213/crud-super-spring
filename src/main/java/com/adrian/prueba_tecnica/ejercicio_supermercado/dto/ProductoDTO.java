package com.adrian.prueba_tecnica.ejercicio_supermercado.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    @NotNull
    @NotBlank
    private String nombre;
    @NotNull
    @NotBlank
    private String categoria;
    @NotNull
  
    @Positive
    private BigDecimal precio;
    @NotNull
    @Positive
    private Integer cantidad;
    @NotNull
    private boolean activo;
}
