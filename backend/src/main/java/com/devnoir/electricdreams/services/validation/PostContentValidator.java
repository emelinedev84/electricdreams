package com.devnoir.electricdreams.services.validation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.PostRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PostContentValidator implements ConstraintValidator<PostContentValid, PostContentDTO> {

	private PostRepository postRepository;

	public PostContentValidator() {
		// No-args constructor required by Hibernate Validator
	}

	@Autowired
	public void setPostRepository(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Override
	public void initialize(PostContentValid constraintAnnotation) {
		// Initialization logic if needed
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isValid(PostContentDTO content, ConstraintValidatorContext context) {
		if (content == null) {
			return true;
		}

		boolean isValid = true;
		context.disableDefaultConstraintViolation();

		// Validate categories
		if (content.getCategories() == null || content.getCategories().isEmpty()) {
			context.buildConstraintViolationWithTemplate("At least one category is required")
				.addPropertyNode("categories")
				.addConstraintViolation();
			isValid = false;
		}

		// Validate tags
        if (content.getTags() != null) {
            Set<String> tagNames = new HashSet<>();
            for (var tag : content.getTags()) {
                if (tag.getName() != null && !tag.getName().trim().isEmpty()) {
                    String normalizedName = tag.getName().trim().toLowerCase();
                    if (!tagNames.add(normalizedName)) {
                        context.buildConstraintViolationWithTemplate("Duplicate tag: " + tag.getName())
                            .addPropertyNode("tags")
                            .addConstraintViolation();
                        isValid = false;
                    }
                }
            }
        }

		// Validate URL handle uniqueness - only for new content or if content ID is different
        if (content.getUrlHandle() != null && !content.getUrlHandle().trim().isEmpty() && content.getLanguage() != null) {
            try {
                Language language = Language.valueOf(content.getLanguage().toUpperCase());
                Optional<Post> existingPost = postRepository.findByContentsUrlHandleAndContentsLanguage(
                    content.getUrlHandle().trim(), language);

                if (existingPost.isPresent()) {
                    PostContent existingContent = existingPost.get().getContents().stream()
                        .filter(c -> c.getLanguage() == language)
                        .findFirst()
                        .orElse(null);

                    if (existingContent != null && (content.getId() == null || !content.getId().equals(existingContent.getId()))) {
                        context.buildConstraintViolationWithTemplate("URL handle already exists for this language: " + content.getLanguage())
                            .addPropertyNode("urlHandle")
                            .addConstraintViolation();
                        isValid = false;
                    }
                }
            } catch (IllegalArgumentException e) {
                context.buildConstraintViolationWithTemplate("Invalid language: " + content.getLanguage())
                    .addPropertyNode("language")
                    .addConstraintViolation();
                isValid = false;
            }
        }

		// Validate content per language - only for new content
		if (content.getLanguage() != null && content.getId() == null) {
			Post existingPost = postRepository.findByContentsLanguage(Language.valueOf(content.getLanguage().toUpperCase()))
				.orElse(null);

			if (existingPost != null) {
				PostContent existingContent = existingPost.getContents().stream()
					.filter(c -> c.getLanguage() == Language.valueOf(content.getLanguage().toUpperCase()))
					.findFirst()
					.orElse(null);

				if (existingContent != null) {
					context.buildConstraintViolationWithTemplate("Only one content per language is allowed")
						.addPropertyNode("language")
						.addConstraintViolation();
					isValid = false;
				}
			}
		}

		return isValid;
	}
}