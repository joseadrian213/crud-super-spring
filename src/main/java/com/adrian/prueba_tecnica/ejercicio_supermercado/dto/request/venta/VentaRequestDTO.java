package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta;

import java.util.List;

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
public class VentaRequestDTO {

    @NotNull
    private EstadoVentaEnum estado;

    // Datos de la sucursal
    @NotNull
    private Long idSucursal;

    // lista de detalles de la venta
    @NotNull
    private List<DetalleVentaRequestDTO> detalle;

   
}
