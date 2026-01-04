package com.devnoir.electricdreams.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.services.AdminTagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/admin/tags")
public class AdminTagResource {

	@Autowired
	public AdminTagService adminTagService;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@GetMapping
	public ResponseEntity<List<TagDTO>> findAllByLanguage(String language) {
		List<TagDTO> list = adminTagService.findAllByLanguage(language);
		return ResponseEntity.ok().body(list);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<TagDTO> findById(@PathVariable Long id) {
		TagDTO dto = adminTagService.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@PostMapping
	public ResponseEntity<TagDTO> create(@Valid @RequestBody TagDTO dto) {
		dto = adminTagService.create(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<TagDTO> update(@PathVariable Long id, @Valid @RequestBody TagDTO tagDto) {
		TagDTO dto = new TagDTO();
		dto.setName(tagDto.getName());
		dto = adminTagService.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WRITER')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		adminTagService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
