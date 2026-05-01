package com.devnoir.blog.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.blog.dto.PublicPostDetailDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.services.BlogService;

@RestController
@RequestMapping(value = "/{language}/posts")
public class BlogResource {

	@Autowired
	private BlogService blogService;
	
	@GetMapping
	public ResponseEntity<Page<PublicPostSummaryDTO>> findAllPublicPosts(@PathVariable String language, Pageable pageable) {
		Page<PublicPostSummaryDTO> page = blogService.findAllPublicPosts(language, pageable);
		return ResponseEntity.ok().body(page);
	}
	
	@GetMapping(value = "/{urlHandle}")
	public ResponseEntity<PublicPostDetailDTO> findPostByUrlHandleAndLanguage(@PathVariable String urlHandle, @PathVariable String language) {
		PublicPostDetailDTO dto = blogService.findPostByUrlHandleAndLanguage(urlHandle, language);
		return ResponseEntity.ok().body(dto);
	}
}
