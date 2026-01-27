package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta;



import jakarta.validation.constraints.NotNull;
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
public class SaleDetailRequestDTO {  
    @NotNull
    @Positive
    private Integer ProductQuantity;
   
    @NotNull
    private Long idProduct;

}
