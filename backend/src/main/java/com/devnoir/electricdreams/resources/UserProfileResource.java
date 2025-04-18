package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.UserDTO;
import com.devnoir.electricdreams.dto.UserProfileDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@RestController
@RequestMapping(value = "/profile")
public class UserProfileResource {

	@Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getOwnProfile() {
        // Como ainda não temos autenticação, vamos criar um usuário de teste se não existir
        User user = userRepository.findByUsername("testuser")
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername("testuser");
                newUser.setEmail("testuser@example.com");
                newUser.setFirstName("Test");
                newUser.setLastName("User");
                return userRepository.save(newUser);
            });
        return ResponseEntity.ok(new UserDTO(user));
    }

    @PutMapping(value = "/me")
    public ResponseEntity<?> updateOwnProfile(@RequestBody UserProfileDTO dto) {
        // Validação básica de email
        if (dto.getEmail() != null && !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.unprocessableEntity().build();
        }

        User user = userRepository.findByUsername("testuser")
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getImageUrl() != null) user.setImageUrl(dto.getImageUrl());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        
        user = userRepository.save(user);
        return ResponseEntity.ok(new UserDTO(user));
    }
}
