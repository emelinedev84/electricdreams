package com.devnoir.electricdreams.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.RoleDTO;
import com.devnoir.electricdreams.dto.UserDTO;
import com.devnoir.electricdreams.dto.UserCreateDTO;
import com.devnoir.electricdreams.entities.Role;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.RoleRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.services.exceptions.DatabaseException;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminUserService {

	@Autowired
 	private UserRepository userRepository;
	
 	@Autowired
 	private RoleRepository roleRepository;
 	
 	@Autowired
 	private BCryptPasswordEncoder passwordEncoder;
 	
 	@Transactional(readOnly = true)
 	public Page<UserDTO> findAllPaged(Pageable pageable) {
 		Page<User> page = userRepository.findAll(pageable);
 		return page.map(x -> new UserDTO(x));
 	}
 	
 	@Transactional(readOnly = true) 
 	public UserDTO findById(Long id) {
 		Optional<User> optional = userRepository.findById(id);
 		User user = optional.orElseThrow(() -> new ResourceNotFoundException("Entity not found")); 
 		return new UserDTO(user);
 	}
 	
 	@Transactional
 	public UserDTO insert(UserCreateDTO dto) {
 		User user = new User();
 		copyDtoToEntity(dto, user);
 		user.setPassword(passwordEncoder.encode(dto.getPassword()));
 		user = userRepository.save(user);
 		return new UserDTO(user);
 	}
 	
 	@Transactional
 	public UserDTO update(Long id, UserDTO dto) {
 		try {
 			User user = userRepository.getReferenceById(id);
 			copyDtoToEntity(dto, user);
 			user = userRepository.save(user);
 			return new UserDTO(user);
 		} catch (EntityNotFoundException e) {
 			throw new ResourceNotFoundException("Id not found: " + id);
 		}
 	}
 	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
		try {
			userRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
 	private void copyDtoToEntity(UserDTO dto, User user) {
 		user.setUsername(dto.getUsername());
 		user.setFirstName(dto.getFirstName());
 		user.setLastName(dto.getLastName());
 		user.setEmail(dto.getEmail());
 		user.getRoles().clear();
 		for(RoleDTO roleDto : dto.getRoles()) {
 			Role role = roleRepository.getReferenceById(roleDto.getId());
 			user.getRoles().add(role);
 		}
 	}
}