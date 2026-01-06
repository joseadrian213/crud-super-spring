package com.adrian.prueba_tecnica.ejercicio_supermercado.dto;


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
public class SucursalDTO {
    private Long id;
    @NotBlank
    @NotNull
    private String nombre;
    @NotBlank
    @NotNull
    private String direccion;
  
    @NotNull
    private boolean activo;

}
