package com.devnoir.electricdreams.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devnoir.electricdreams.dto.UserUpdateDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.resources.exceptions.FieldMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
 	private HttpServletRequest request;
	
	@Autowired
 	private UserRepository userRepository;
 		
	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Validação de email único (exceto para o próprio usuário)
		if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
			Optional<User> user = userRepository.findByEmail(dto.getEmail());
			if (user != null && userId != user.get().getId()) {
				list.add(new FieldMessage("email", "Email already exists"));
			} 
		}
		
		// Validação de username único (exceto para o próprio usuário)
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            Optional<User> user = userRepository.findByUsername(dto.getUsername());
            if (user != null && userId != user.get().getId()) {
                list.add(new FieldMessage("username", "Username already exists"));
            } 
        }
        
	    for (FieldMessage e : list) {
	        context.disableDefaultConstraintViolation();
	        context.buildConstraintViolationWithTemplate(e.getMessage())
	            .addPropertyNode(e.getFieldName())
	            .addConstraintViolation();
	    }
	    return list.isEmpty();
	}

}