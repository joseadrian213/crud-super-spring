package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un rol o perfil de usuario en el sistema.
 * 
 * Esta clase modela los diferentes roles que pueden asignarse a los usuarios,
 * determinando los permisos y funcionalidades que cada usuario puede acceder.
 * Un rol puede ser asignado a múltiples usuarios.
 * 
 * @author Adrian
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "roles")
@EqualsAndHashCode
public class Role {
    /**
     * Identificador único del rol.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    /**
     * Nombre del rol.
     * Identifica de forma única el tipo de rol (ej: ADMIN, USER, MANAGER).
     * Este campo es único en la base de datos.
     */
    @Column(unique = true)
    private String name; 

    /**
     * Lista de usuarios que tienen asignado este rol.
     * Relación muchos-a-muchos (lado inverso): múltiples usuarios pueden tener este rol,
     * y cada usuario puede tener múltiples roles.
     * 
     * @see User
     */
    @JsonIgnoreProperties({"roles", "handler","hibernateLazyInitializer"})
    @ManyToMany(mappedBy = "roles")
    private List<User> users; 

}
