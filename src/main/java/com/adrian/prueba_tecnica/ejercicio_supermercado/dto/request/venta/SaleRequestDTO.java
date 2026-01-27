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
public class SaleRequestDTO {

    @NotNull
    private EstadoVentaEnum status;

    // Datos de la sucursal
    @NotNull
    private Long idBranch;

    // lista de detalles de la venta
    @NotNull
    private List<SaleDetailRequestDTO> detail;

   
}
