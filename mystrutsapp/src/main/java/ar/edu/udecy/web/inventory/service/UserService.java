package ar.edu.udecy.web.inventory.service;

import ar.edu.udecy.web.inventory.dto.UserRequestDTO;
import ar.edu.udecy.web.inventory.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(String username);
}