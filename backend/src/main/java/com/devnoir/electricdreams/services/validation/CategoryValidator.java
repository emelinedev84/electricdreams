package com.devnoir.electricdreams.services.validation;

import java.util.ArrayList;
import java.util.List;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.resources.exceptions.FieldMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryValidator  implements ConstraintValidator<CategoryValid, CategoryDTO>  {

    @Override
    public boolean isValid(CategoryDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> list = new ArrayList<>();

        // Validação de idioma
        if (dto.getLanguage() == null || dto.getLanguage().isBlank()) {
            list.add(new FieldMessage("language", "Language is required"));
        } else {
            try {
                Language.valueOf(dto.getLanguage().toUpperCase());
            } catch (IllegalArgumentException e) {
                list.add(new FieldMessage("language", "Invalid language: " + dto.getLanguage()));
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