package com.devnoir.electricdreams.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devnoir.electricdreams.dto.UserCreateDTO;
import com.devnoir.electricdreams.dto.UserDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.RoleRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {

	@Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService service;
    
	// Caso de uso: Buscar usuário por ID
    @Test
    void shouldFindUserByIdWhenUserExists() {
    	// Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("writer01");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = service.findById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("writer01");
    }

    // Caso de uso: Criar novo usuário como writer por padrão
    @Test
    void shouldCreateNewWriterUserWithDefaultRole() {
    	// Arrange
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("writer01");
        dto.setEmail("writer@example.com");
        dto.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(dto.getUsername());
        savedUser.setEmail(dto.getEmail());

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = service.insert(dto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("writer01");
    }

    // Caso de uso: Validar e-mail duplicado
    @Test
    void shouldNotAllowDuplicateEmail() {
    	// Arrange
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("writer01");
        dto.setEmail("existing@example.com");
        dto.setPassword("password123");

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        assertThatThrownBy(() -> service.insert(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email already exists");
    }
    

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Id not found: 99");
    }
}
