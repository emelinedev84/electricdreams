package com.devnoir.blog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.blog.dto.CategoryPublicDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.services.BlogCategoryService;

@RestController
@RequestMapping(value = "/{language}/categories")
public class BlogCategoryResource {

	@Autowired
	public BlogCategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<List<CategoryPublicDTO>> findAll(@PathVariable Language language, Pageable pageable) {
		List<CategoryPublicDTO> list = categoryService.findAll(language);
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{categoryCode}/posts")
	public ResponseEntity<Page<PublicPostSummaryDTO>> findPostsByCategory(@PathVariable String language, @PathVariable String categoryCode, Pageable pageable) {
		Page<PublicPostSummaryDTO> page = categoryService.findPostsByCategory(language, categoryCode, pageable);
		return ResponseEntity.ok().body(page);
	}
}