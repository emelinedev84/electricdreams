package com.devnoir.electricdreams.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.TagRepository;
import com.devnoir.electricdreams.resources.exceptions.FieldMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TagValidator  implements ConstraintValidator<TagValid, TagDTO>  {

	@Autowired
    private TagRepository tagRepository;

    @Override
    public boolean isValid(TagDTO dto, ConstraintValidatorContext context) {
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

        // Validação de unicidade (exceto para a própria tag)
        if (dto.getName() != null && dto.getLanguage() != null) {
            try {
                Language language = Language.valueOf(dto.getLanguage().toUpperCase());
                Optional<Tag> existing = tagRepository.findByNameAndLanguage(
                    dto.getName().toLowerCase(), language);
                if (existing.isPresent() && (dto.getId() == null || !existing.get().getId().equals(dto.getId()))) {
                    list.add(new FieldMessage("name", "Tag already exists in this language"));
                }
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