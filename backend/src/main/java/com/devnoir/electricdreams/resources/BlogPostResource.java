package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.BlogPostDTO;
import com.devnoir.electricdreams.services.BlogPostService;

@RestController
@RequestMapping(value = "/posts")
public class BlogPostResource {
	
	@Autowired
	public BlogPostService postService;
	
	@GetMapping
	public ResponseEntity<Page<BlogPostDTO>> findAll(Pageable pageable) {
		Page<BlogPostDTO> page = postService.findAllPaged(pageable);
		return ResponseEntity.ok().body(page);
	}

}
