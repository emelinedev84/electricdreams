package com.devnoir.electricdreams.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.services.PostContentService;

@RestController
@RequestMapping(value = "/contents")
public class PostContentResource {
	
	@Autowired
	public PostContentService contentService;
	
	@GetMapping
	public ResponseEntity<Page<PostContentDTO>> findAll(Pageable pageable) {
		Page<PostContentDTO> page = contentService.findAllPaged(pageable);
		return ResponseEntity.ok().body(page);
	}

}
