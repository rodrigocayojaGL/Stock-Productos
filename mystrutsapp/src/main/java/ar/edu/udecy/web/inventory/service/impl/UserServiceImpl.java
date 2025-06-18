package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.dto.UserRequestDTO;
import ar.edu.udecy.web.inventory.dto.UserResponseDTO;
import ar.edu.udecy.web.inventory.entity.UserEntity;
import ar.edu.udecy.web.inventory.handler.exception.UserNotFoundException;
import ar.edu.udecy.web.inventory.repository.UserRepository;
import ar.edu.udecy.web.inventory.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        UserEntity savedUser = userRepository.save(UserEntity.builder().username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .roles(userRequestDTO.getRoles())
                .build());
        return mapToResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return mapToResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(userRequestDTO.getPassword());
        user.setRoles(userRequestDTO.getRoles());
        UserEntity updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
            userRepository.deleteById(user.getId());
    }

    private UserResponseDTO mapToResponseDTO(UserEntity user) {
        return new UserResponseDTO(user.getUsername(), user.getRoles());
    }
}