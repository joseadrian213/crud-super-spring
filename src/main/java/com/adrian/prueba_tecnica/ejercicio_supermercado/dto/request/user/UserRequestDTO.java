package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.user;

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
public class UserRequestDTO {

    @NotNull
    private String username;
    @NotBlank
    private String password;
    private Boolean admin;
    private Boolean enabled;
}
