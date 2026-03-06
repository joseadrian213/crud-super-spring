package com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user;

import java.time.LocalDateTime;
import java.util.List;

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
public class UserResponseDTO {
    private Long id;
    @NotBlank
    @NotNull
    private String username;
    private Boolean admin;
    @NotBlank
    @NotNull
    private List<RoleResponseDTO> roles;
    private Boolean enabled;
    private LocalDateTime creationDate;
    private LocalDateTime UpdateDate;
}
