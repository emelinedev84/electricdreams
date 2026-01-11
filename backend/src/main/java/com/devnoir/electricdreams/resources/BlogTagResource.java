package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.services.BlogTagService;

@RestController
@RequestMapping(value = "/{language}/tags")
public class BlogTagResource {

	@Autowired
	public BlogTagService tagService;
	
	@GetMapping
	public ResponseEntity<Page<TagDTO>> findAll(@PathVariable String language, Pageable pageable) {
		Page<TagDTO> page = tagService.findAllByLanguage(language, pageable);
		return ResponseEntity.ok().body(page);
	}
}