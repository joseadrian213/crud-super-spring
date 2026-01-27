package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta;

import java.time.LocalDate;

import com.adrian.prueba_tecnica.ejercicio_supermercado.enums.EstadoVentaEnum;

import jakarta.validation.constraints.NotNull;
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
public class SaleUpdateRequestDTO {
   @NotNull
    private LocalDate date;
    @NotNull
    private EstadoVentaEnum status;
}
