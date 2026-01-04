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

import com.devnoir.electricdreams.dto.UserCreateDTO;
import com.devnoir.electricdreams.dto.UserDTO;
import com.devnoir.electricdreams.dto.UserProfileDTO;
import com.devnoir.electricdreams.dto.UserRoleDTO;
import com.devnoir.electricdreams.services.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class AdminUserResource {

	@Autowired
	public AdminUserService adminUserService;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
		Page<UserDTO> page = adminUserService.findAllPaged(pageable);
		return ResponseEntity.ok().body(page);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		UserDTO dto = adminUserService.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserCreateDTO dto) {
		UserDTO newDto = adminUserService.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();
		return ResponseEntity.created(uri).body(newDto);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
		UserDTO dto = adminUserService.update(id, userDto);
		return ResponseEntity.ok().body(dto);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}/role")
    public ResponseEntity<UserDTO> updateRole(@PathVariable Long id, @Valid @RequestBody UserRoleDTO dto) {
        UserDTO userDTO = adminUserService.updateRole(id, dto);
        return ResponseEntity.ok().body(userDTO);
    }
    
	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/profile")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody UserProfileDTO dto) {
        UserDTO userDTO = adminUserService.updateProfile(id, dto);
        return ResponseEntity.ok().body(userDTO);
    }
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		adminUserService.delete(id);
		return ResponseEntity.noContent().build();
	}
}