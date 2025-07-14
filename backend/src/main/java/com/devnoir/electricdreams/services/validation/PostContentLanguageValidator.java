package com.devnoir.electricdreams.services.validation;

import org.springframework.stereotype.Component;

import com.devnoir.electricdreams.dto.PostCreateDTO;
import com.devnoir.electricdreams.enums.Language;

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
		if (dto.getEn() == null && dto.getPt() == null) {
			context.buildConstraintViolationWithTemplate("At least one language content (EN or PT) is required")
				.addConstraintViolation();
			return false;
		}

		// Validar conteúdo em inglês
        if (dto.getEn() != null) {
            if (dto.getEn().getLanguage() == null) {
                context.buildConstraintViolationWithTemplate("Language must be set for English content")
                    .addPropertyNode("en.language")
                    .addConstraintViolation();
                isValid = false;
            } else if (!Language.EN.name().equals(dto.getEn().getLanguage())) {
                context.buildConstraintViolationWithTemplate("English content must have language set to EN")
                    .addPropertyNode("en.language")
                    .addConstraintViolation();
                isValid = false;
            }
        }

     // Validar conteúdo em português
        if (dto.getPt() != null) {
            if (dto.getPt().getLanguage() == null) {
                context.buildConstraintViolationWithTemplate("Language must be set for Portuguese content")
                    .addPropertyNode("pt.language")
                    .addConstraintViolation();
                isValid = false;
            } else if (!Language.PT.name().equals(dto.getPt().getLanguage())) {
                context.buildConstraintViolationWithTemplate("Portuguese content must have language set to PT")
                    .addPropertyNode("pt.language")
                    .addConstraintViolation();
                isValid = false;
            }
        }

		return isValid;
	}
}