package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BranchRequestDTO {
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String address;
      
    private Boolean active;
}
