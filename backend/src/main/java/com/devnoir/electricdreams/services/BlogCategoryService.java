package com.devnoir.electricdreams.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;

@Service
public class BlogCategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllByLanguage(String language, Pageable pageable) {
		try {
            Language lang = Language.valueOf(language.toUpperCase());
            Page<Category> page = categoryRepository.findByLanguage(lang, pageable);
            return page.map(x -> new CategoryDTO(x));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid language: " + language);
        }
    }
}