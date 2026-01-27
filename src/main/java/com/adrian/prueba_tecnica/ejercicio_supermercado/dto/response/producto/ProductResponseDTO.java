package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime updateCreation;
}
