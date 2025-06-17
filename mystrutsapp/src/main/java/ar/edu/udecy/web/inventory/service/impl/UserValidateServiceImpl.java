package ar.edu.udecy.web.inventory.service.impl;
import ar.edu.udecy.web.inventory.config.JwtUtil;

import ar.edu.udecy.web.inventory.dto.UserResponseDTO;
import ar.edu.udecy.web.inventory.dto.UserValidateResponseDTO;
import ar.edu.udecy.web.inventory.entity.UserEntity;
import ar.edu.udecy.web.inventory.handler.exception.InvalidCredentialsException;
import ar.edu.udecy.web.inventory.repository.UserRepository;
import ar.edu.udecy.web.inventory.service.UserValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserValidateServiceImpl implements UserValidateService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserValidateResponseDTO isValidUser(String username, String password) {
        // Fetch user from the database
        Optional<UserEntity> user = userRepository.findByUsername(username);

        // Validate user existence and password
         boolean validateUser = user.isPresent() && user.get().getPassword().equals(password);

        // Validate user credentials
        if (!validateUser) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(username, user.get().getRoles());

        // Return user details with token
        return new UserValidateResponseDTO(username, user.get().getRoles(), token);
    }


}
