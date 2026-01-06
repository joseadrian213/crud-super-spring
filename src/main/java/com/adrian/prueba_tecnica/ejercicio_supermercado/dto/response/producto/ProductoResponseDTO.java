package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto;

import java.math.BigDecimal;


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
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private Integer cantidad;
    private boolean activo;
}
