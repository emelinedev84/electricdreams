package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.services.BlogCategoryService;

@RestController
@RequestMapping(value = "/{language}/categories")
public class BlogCategoryResource {

	@Autowired
	public BlogCategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<Page<CategoryDTO>> findAll(@PathVariable String language, Pageable pageable) {
		Page<CategoryDTO> page = categoryService.findAllByLanguage(language, pageable);
		return ResponseEntity.ok().body(page);
	}
}