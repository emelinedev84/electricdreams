package com.devnoir.blog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devnoir.blog.dto.UserUpdateDTO;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.repositories.UserRepository;
import com.devnoir.blog.resources.exceptions.FieldMessage;

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
			if (user.isPresent() && userId != user.get().getId()) {
				list.add(new FieldMessage("email", "Email already exists"));
			} 
		}
		
		// Validação de username único (exceto para o próprio usuário)
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            Optional<User> user = userRepository.findByUsername(dto.getUsername());
            if (user.isPresent() && userId != user.get().getId()) {
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