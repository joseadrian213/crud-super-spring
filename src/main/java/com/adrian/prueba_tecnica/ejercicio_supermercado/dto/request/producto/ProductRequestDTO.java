package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto;

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
public class ProductRequestDTO {
  
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String category;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotNull
    @Positive
    private Integer stock;
    
    private Boolean active;
}
