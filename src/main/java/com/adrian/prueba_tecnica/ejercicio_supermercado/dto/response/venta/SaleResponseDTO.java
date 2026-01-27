package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.enums.EstadoVentaEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
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
public class SaleResponseDTO {
    // Datos de la vetna
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private EstadoVentaEnum status;

    // Datos de la sucursal
    @NotNull
    private Long idBranch;

    // lista de detalles de la venta
    @NotNull
    private List<SaleDetailResponseDTO> detail;

    // Total de la venta
    @Null
    @Positive
    private BigDecimal total;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}
