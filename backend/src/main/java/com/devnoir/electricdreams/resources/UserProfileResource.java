package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.UserDTO;
import com.devnoir.electricdreams.dto.UserProfileDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.services.AdminUserService;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/profile")
public class UserProfileResource {

	private static final String TEST_USER = "testuser";
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private AdminUserService adminUserService;

	@PreAuthorize("hasRole('ROLE_WRITER')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getOwnProfile() {
        // Como ainda não temos autenticação, vamos criar um usuário de teste se não existir
    	User user = userRepository.findByUsername(TEST_USER)  // Usando a constante
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(TEST_USER);  // Usando a constante
                    newUser.setEmail("test@example.com");
                    newUser.setPassword("password123");
                    return userRepository.save(newUser);
                });
            return ResponseEntity.ok(new UserDTO(user));
    }

	@PreAuthorize("hasRole('ROLE_WRITER')")
    @PutMapping(value = "/me")
    public ResponseEntity<UserDTO> updateOwnProfile(@Valid @RequestBody UserProfileDTO dto) {
        User user = userRepository.findByUsername(TEST_USER)  // Usando a constante
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Usar o serviço ao invés de atualizar diretamente
        return ResponseEntity.ok(adminUserService.updateProfile(user.getId(), dto));
    }
}
