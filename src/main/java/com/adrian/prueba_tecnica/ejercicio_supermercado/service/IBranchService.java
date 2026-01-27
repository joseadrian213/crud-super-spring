package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.BranchRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.BranchResponseDTO;


public interface IBranchService {
    
    List<BranchResponseDTO> findAllBranches();
    
    BranchResponseDTO findByIdBranch(Long id); 
    
    BranchResponseDTO createBranch(BranchRequestDTO branchDTO);

    BranchResponseDTO updateBranch(Long id, BranchRequestDTO branchDTO);

    void deleteBranch(Long id);
}
