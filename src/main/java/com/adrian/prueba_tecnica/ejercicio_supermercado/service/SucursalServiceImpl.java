package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.SucursalRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.SucursalResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sucursal;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SucursalRepository;

/**
 * Implementación del servicio de gestión de sucursales.
 * 
 * Esta clase proporciona la lógica de negocio para operaciones CRUD sobre sucursales,
 * incluyendo validaciones, búsqueda, creación, actualización y eliminación (lógica).
 * Implementa la interfaz {@link ISucursalService}.
 * 
 * Características:
 * - Obtención de lista de sucursales activas
 * - Búsqueda de sucursal por identificador
 * - Creación de nuevas sucursales con validaciones
 * - Actualización parcial de información de sucursales
 * - Eliminación lógica de sucursales (marcado como inactivo)
 * 
 * @author Adrian
 * @version 1.0
 * @see ISucursalService
 * @see Sucursal
 */
@Service
public class SucursalServiceImpl implements ISucursalService {
    /**
     * Repositorio para acceder a la información de sucursales en la base de datos.
     */
    private SucursalRepository repository;

    /**
     * Constructor que inyecta el repositorio de sucursales.
     * 
     * @param sucursalRepository repositorio para acceder a datos de sucursales
     */
    public SucursalServiceImpl(SucursalRepository sucursalRepository) {
        this.repository = sucursalRepository;
    }

    /**
     * Obtiene la lista de todas las sucursales disponibles.
     * 
     * Este método busca todas las sucursales en la base de datos y las convierte
     * a objetos DTO para su devolución. Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link SucursalResponseDTO} con información de todas las sucursales
     */
    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> traerSucursales() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    /**
     * Obtiene una sucursal específica por su identificador.
     * 
     * @param id identificador único de la sucursal a buscar
     * @return {@link SucursalResponseDTO} con la información de la sucursal
     * @throws NotFoundException si la sucursal no existe en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDTO geSucursalDTO(Long id) {
        return repository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }

    /**
     * Crea una nueva sucursal con los datos proporcionados.
     * 
     * Valida que:
     * - El DTO de solicitud no sea nulo
     * - El nombre sea obligatorio y no esté en blanco
     * - La dirección sea obligatoria y no esté en blanco
     * 
     * @param sucursalRequestDTO objeto con los datos de la sucursal a crear
     * @return {@link SucursalResponseDTO} con los datos de la sucursal creada
     * @throws IllegalArgumentException si alguna validación falla
     * 
     * @see SucursalRequestDTO
     */
    @Override
    @Transactional
    public SucursalResponseDTO crearSucursal(SucursalRequestDTO sucursalRequestDTO) {
        if (sucursalRequestDTO == null)
            throw new IllegalArgumentException("SucursalRequestDTO es null");

        if (sucursalRequestDTO.getNombre() == null || sucursalRequestDTO.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (sucursalRequestDTO.getDireccion() == null || sucursalRequestDTO.getDireccion().isBlank())
            throw new IllegalArgumentException("La direccion es obligatoria");

        Sucursal sucursal = Sucursal.builder()
                .nombre(sucursalRequestDTO.getNombre())
                .direccion(sucursalRequestDTO.getDireccion())
                .activo(sucursalRequestDTO.getActivo())
                .build();
        return Mapper.toDTO(repository.save(sucursal));
    }

    /**
     * Actualiza la información de una sucursal existente.
     * 
     * Solo actualiza los campos que son proporcionados en el DTO de solicitud.
     * Si un campo es nulo, no se modifica.
     * 
     * @param id identificador único de la sucursal a actualizar
     * @param sucursalRequestDTO objeto con los datos de la sucursal a actualizar
     * @return {@link SucursalResponseDTO} con los datos de la sucursal actualizada
     * @throws NotFoundException si la sucursal no existe en la base de datos
     * 
     * @see SucursalRequestDTO
     */
    @Override
    @Transactional
    public SucursalResponseDTO actualizarSucursal(Long id, SucursalRequestDTO sucursalRequestDTO) {
        Sucursal sucursal = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));

        if (sucursalRequestDTO.getNombre() != null)
            sucursal.setNombre(sucursalRequestDTO.getNombre());

        if (sucursalRequestDTO.getDireccion() != null)
            sucursal.setDireccion(sucursalRequestDTO.getDireccion());

        if (sucursalRequestDTO.getActivo() != null)
            sucursal.setActivo(sucursalRequestDTO.getActivo());
        
        return Mapper.toDTO(repository.save(sucursal));
    }

    /**
     * Elimina (desactiva) una sucursal específica.
     * 
     * Esta operación realiza una eliminación lógica: marca la sucursal como inactiva
     * en lugar de borrarla físicamente de la base de datos.
     * 
     * @param id identificador único de la sucursal a eliminar
     * @throws NotFoundException si la sucursal no existe en la base de datos
     */
    @Override
    @Transactional
    public void eliminarSucursal(Long id) {

        Sucursal sucursal = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("sucursal no encontrada"));
        sucursal.setActivo(false);
        repository.save(sucursal);

    }

}
