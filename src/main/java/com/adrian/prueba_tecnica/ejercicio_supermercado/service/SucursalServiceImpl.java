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

@Service
public class SucursalServiceImpl implements ISucursalService {
    private SucursalRepository repository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository) {
        this.repository = sucursalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> traerSucursales() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDTO geSucursalDTO(Long id) {
        return repository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }

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

    @Override
    @Transactional
    public void eliminarSucursal(Long id) {

        Sucursal sucursal = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("sucursal no encontrada"));
        sucursal.setActivo(false);
        repository.save(sucursal);

    }

}
