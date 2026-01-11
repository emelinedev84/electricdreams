package com.devnoir.electricdreams.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.devnoir.electricdreams.dto.UserCreateDTO;
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
 			if (userRepository.existsByEmail(dto.getEmail())) {
 	            list.add(new FieldMessage("email", "Email already exists"));
 	        }
 		}
 		
 	    // Validação de username único
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
        	if (userRepository.existsByUsername(dto.getUsername())) {
                list.add(new FieldMessage("username", "Username already exists"));
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
