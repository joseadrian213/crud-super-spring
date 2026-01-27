package com.adrian.prueba_tecnica.ejercicio_supermercado.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un usuario del sistema.
 * 
 * Esta clase modela la información de un usuario, incluyendo sus credenciales de acceso,
 * roles asignados y estado de habilitación. Los usuarios pueden tener múltiples roles
 * que determinan sus permisos en el sistema.
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
@Table(name = "users")
@EqualsAndHashCode
public class User {
    /**
     * Identificador único del usuario.
     * Se genera automáticamente usando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único.
     * Se utiliza para identificar el usuario en la autenticación.
     * Este campo es único en la base de datos.
     */
    @Column(unique = true)
    private String username;

    /**
     * Contraseña del usuario.
     * Se codifica y almacena de forma segura.
     * No se incluye en las respuestas JSON (solo lectura en escritura).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Indicador de si el usuario es administrador.
     * Este campo es transitorio y no se persiste en la base de datos.
     * Se utiliza solo en operaciones de lectura/escritura.
     */
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean admin;

    /**
     * Lista de roles asignados al usuario.
     * Relación muchos-a-muchos: un usuario puede tener múltiples roles,
     * y un rol puede ser asignado a múltiples usuarios.
     * La relación se almacena en la tabla intermedia 'users_roles'.
     * Se garantiza que un usuario no tendrá el mismo rol asignado más de una vez.
     * 
     * @see Role
     */
    @JsonIgnoreProperties({ "users", "handler", "hibernateLazyInitializer" })
    @ManyToMany
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "user_id", "role_id" })
    })
    private List<Role> roles;

    /**
     * Indica si el usuario está habilitado en el sistema.
     * Un usuario deshabilitado no puede acceder al sistema.
     */
    private Boolean enabled;

    /**
     * Fecha y hora en la que se creó el registro del usuario.
     * Se asigna automáticamente mediante {@link #prePersist()} y no puede ser actualizada.
     */
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    /**
     * Fecha y hora de la última actualización del registro del usuario.
     * Se actualiza automáticamente mediante {@link #preUpdate()}.
     */
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    /**
     * Método de ciclo de vida que se ejecuta antes de persistir la entidad.
     * Asigna la fecha y hora actual a {@code creationDate} si no está establecida,
     * y actualiza {@code updateDate} con la fecha y hora actual.
     */
    @PrePersist
    protected void prePersist() {
        if (this.creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        updateDate = LocalDateTime.now();
    }

    /**
     * Método de ciclo de vida que se ejecuta antes de actualizar la entidad.
     * Actualiza {@code updateDate} con la fecha y hora actual.
     */
    @PreUpdate
    protected void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
