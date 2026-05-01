package com.devnoir.blog.services.validation;

import org.springframework.stereotype.Component;

import com.devnoir.blog.dto.PostCreateDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PostContentLanguageValidator implements ConstraintValidator<PostContentLanguageValid, PostCreateDTO> {

	@Override
	public boolean isValid(PostCreateDTO dto, ConstraintValidatorContext context) {
		if (dto == null) {
			return true;
		}

		boolean isValid = true;
		context.disableDefaultConstraintViolation();

		// Check if at least one language is provided
		if (dto.getEn() == null) {
			context.buildConstraintViolationWithTemplate("EN content is required")
				.addPropertyNode("en")
				.addConstraintViolation();
			return false;
		}

		return isValid;
	}
}