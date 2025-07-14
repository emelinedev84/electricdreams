package com.devnoir.electricdreams.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.devnoir.electricdreams.dto.UserCreateDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.resources.exceptions.FieldMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserCreateValidator implements ConstraintValidator<UserCreateValid, UserCreateDTO> {

	@Autowired
 	private UserRepository userRepository;
 		
 	@Override
	public boolean isValid(UserCreateDTO dto, ConstraintValidatorContext context) {
 		List<FieldMessage> list = new ArrayList<>();
 		
 		 // Validação de email único
 		if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
 			Optional<User> user = userRepository.findByEmail(dto.getEmail());
 			if (user != null) {
 				list.add(new FieldMessage("email", "Email already exists"));
 			}
 		}
 		
 	    // Validação de username único
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            Optional<User> user = userRepository.findByUsername(dto.getUsername());
            if (user != null) {
                list.add(new FieldMessage("username", "Username already exists"));
            }
        }
        
        // Validação de força da senha (complementar ao Bean Validation)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 8) {
                list.add(new FieldMessage("password", "Password must be at least 8 characters"));
            }
            if (!dto.getPassword().matches(".*[A-Z].*")) {
                list.add(new FieldMessage("password", "Password must contain at least one uppercase letter"));
            }
            if (!dto.getPassword().matches(".*[a-z].*")) {
                list.add(new FieldMessage("password", "Password must contain at least one lowercase letter"));
            }
            if (!dto.getPassword().matches(".*\\d.*")) {
                list.add(new FieldMessage("password", "Password must contain at least one number"));
            }
            if (!dto.getPassword().matches(".*[@$!%*?&].*")) {
                list.add(new FieldMessage("password", "Password must contain at least one special character"));
            }
        }
 		
 		for (FieldMessage e : list) {
 			context.disableDefaultConstraintViolation();
 			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
 					.addConstraintViolation();
 		}
 		return list.isEmpty();
	}

}
