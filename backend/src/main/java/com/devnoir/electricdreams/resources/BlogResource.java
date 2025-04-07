package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.PostDTO;
import com.devnoir.electricdreams.services.BlogService;

@RestController
@RequestMapping(value = "/{language}")
public class BlogResource {

	@Autowired
	private BlogService blogService;
	
	@GetMapping
	public ResponseEntity<Page<PostDTO>> findAllPublicPosts(@PathVariable String language, Pageable pageable) {
		Page<PostDTO> page = blogService.findAllPublicPosts(language, pageable);
		return ResponseEntity.ok().body(page);
	}
	
	@GetMapping(value = "/posts/{urlHandle}")
	public ResponseEntity<PostDTO> findPostByUrlHandleAndLanguage(@PathVariable String urlHandle, @PathVariable String language) {
		PostDTO dto = blogService.findPostByUrlHandleAndLanguage(urlHandle, language);
		return ResponseEntity.ok().body(dto);
	}
}
