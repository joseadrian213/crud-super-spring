package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal;


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
public class BranchResponseDTO {
    private Long id;
    private String name;
    private String address;
    private boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}
