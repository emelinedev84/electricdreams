package com.devnoir.electricdreams.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;
import com.devnoir.electricdreams.services.exceptions.DatabaseException;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminCategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> page = categoryRepository.findAll(pageable);
		return page.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> optional = categoryRepository.findById(id);
		Category category = optional.orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO create(CategoryDTO dto) {
		// Validações de negócio específicas
		validateCategoryCreation(dto);

		Category category = new Category();
		category.setName(dto.getName());
		category.setLanguage(Language.valueOf(dto.getLanguage()));
		category = categoryRepository.save(category);
		return new CategoryDTO(category);

	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		// Validações de negócio específicas
		validateCategoryUpdate(id, dto);

		try {
			Category category = categoryRepository.getReferenceById(id);
			category.setName(dto.getName());
			category = categoryRepository.save(category);
			return new CategoryDTO(category);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
		try {
			categoryRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	private void validateCategoryCreation(CategoryDTO dto) {
		try {
	        if (categoryRepository.findByNameAndLanguage(dto.getName(), Language.valueOf(dto.getLanguage()))
	                .isPresent()) {
	            throw new BusinessException("Categoria já existe neste idioma");
	        }

	        // Validação do idioma
	        if (dto.getLanguage() == null) {
	            throw new BusinessException("Language is required");
	        }

	        // Validação do nome
	        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
	            throw new BusinessException("Category name is required");
	        }

	    } catch (IllegalArgumentException e) {
	        throw new BusinessException("Invalid language: " + dto.getLanguage());
	    }
	}
	
	private void validateCategoryUpdate(Long id, CategoryDTO dto) {
		Optional<Category> existingCategory = categoryRepository.findByNameAndLanguage(dto.getName(),
				Language.valueOf(dto.getLanguage()));
		if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
			throw new BusinessException("Category already exists in this language");
		}
	}
}
