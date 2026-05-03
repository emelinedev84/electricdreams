package com.devnoir.blog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.CategoryDTO;
import com.devnoir.blog.entities.Category;
import com.devnoir.blog.repositories.CategoryRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;
import com.devnoir.blog.utils.SlugifyHelper;

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
		Category category = new Category();
		copyDtoToEntity(dto, category);
		if (categoryRepository.existsByCode(category.getCode())) {
			throw new BusinessException("Category code already exists: " + category.getCode());
		}
		
		category = categoryRepository.save(category);
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
	    Category category = categoryRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
	    String newCode;
	    if (dto.getCode() == null || dto.getCode().isBlank()) {
	    	newCode = SlugifyHelper.toSlug(dto.getNameEn());
	    } else {
	    	newCode = SlugifyHelper.toSlug(dto.getCode());
	    }
	    
	    Optional<Category> categoryWithSameCode = categoryRepository.findByCode(newCode);
	    if (categoryWithSameCode.isPresent() && !categoryWithSameCode.get().getId().equals(id)) {
	    	throw new BusinessException("Category code already exists: " + newCode);
	    }
	    
	    category.setCode(newCode);
    	category.setNameEn(dto.getNameEn());
    	category.setNamePt(dto.getNamePt());
	    category = categoryRepository.save(category);
	    return new CategoryDTO(category);
	}

	@Transactional
	public void delete(Long id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
		if (!category.getPosts().isEmpty()) {
			throw new BusinessException("Cannot delete a category that is being used in posts");
		}
		categoryRepository.delete(category);
	}
	
    private void copyDtoToEntity(CategoryDTO dto, Category category) {
    	if (dto.getCode() == null || dto.getCode().isBlank()) {
    		category.setCode(SlugifyHelper.toSlug(dto.getNameEn()));
    	} else {
    		category.setCode(SlugifyHelper.toSlug(dto.getCode()));
    	}
    	category.setNameEn(dto.getNameEn());
    	category.setNamePt(dto.getNamePt());
    }
}
