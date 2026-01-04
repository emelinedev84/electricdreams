package com.devnoir.electricdreams.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devnoir.electricdreams.dto.PostCreateDTO;
import com.devnoir.electricdreams.dto.PostDTO;
import com.devnoir.electricdreams.dto.PostSummaryDTO;
import com.devnoir.electricdreams.services.AdminPostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/admin/posts")
public class AdminPostResource {
	
	@Autowired
	public AdminPostService adminPostService;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
    @GetMapping
    public ResponseEntity<Page<PostSummaryDTO>> findAllSummary(Pageable pageable) {
        Page<PostSummaryDTO> page = adminPostService.findAllSummary(pageable);
        return ResponseEntity.ok().body(page);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<PostDTO> findById(@PathVariable Long id) {
		PostDTO dto = adminPostService.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@PostMapping
	public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateDTO dto) {
		PostDTO newDto = adminPostService.create(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();
		return ResponseEntity.created(uri).body(newDto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<PostDTO> update(@PathVariable Long id, @Valid @RequestBody PostCreateDTO dto) {
		PostDTO updatedDto = adminPostService.update(id, dto);
		return ResponseEntity.ok().body(updatedDto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Long id) {
		adminPostService.deletePost(id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@DeleteMapping(value = "/{id}/content/{language}")
	public ResponseEntity<Void> deletePostContent(@PathVariable Long id, @PathVariable String language) {
		adminPostService.deletePostContent(id, language);
		return ResponseEntity.noContent().build();
	}
}