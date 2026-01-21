package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;



import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.user.UserRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;


public interface IUserService {
    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id); 

    UserResponseDTO save(UserRequestDTO userRequestDTO);

    Boolean existByUsername(String username);

}
