package com.devnoir.blog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.CategoryPublicDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.CategoryRepository;

@Service
public class BlogCategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private BlogService blogService;
	
	@Transactional(readOnly = true)
    public List<CategoryPublicDTO> findAll(Language language) {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryPublicDTO(
                        c.getCode(),
                        c.getLocalizedName(language)  // ou helper
                ))
                .toList();
    }
	
	@Transactional(readOnly = true)
	public Page<PublicPostSummaryDTO> findPostsByCategory(String language, String categoryCode, Pageable pageable) {
		return blogService.findPublicPostsByCategory(language, categoryCode, pageable);
	}
}