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

import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.dto.TagPublicDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.services.BlogTagService;

@RestController
@RequestMapping(value = "/{language}/tags")
public class BlogTagResource {

	@Autowired
	public BlogTagService tagService;
	
	@GetMapping
	public ResponseEntity<List<TagPublicDTO>> findAll(@PathVariable Language language, Pageable pageable) {
		List<TagPublicDTO> list = tagService.findAll(language);
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{categoryCode}/posts")
	public ResponseEntity<Page<PublicPostSummaryDTO>> findPostsByTag(@PathVariable String language, @PathVariable String tagCode, Pageable pageable) {
		Page<PublicPostSummaryDTO> page = tagService.findPostsByTag(language, tagCode, pageable);
		return ResponseEntity.ok().body(page);
	}
}