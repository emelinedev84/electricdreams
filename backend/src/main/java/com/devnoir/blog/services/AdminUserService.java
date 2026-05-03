package com.devnoir.blog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.RoleDTO;
import com.devnoir.blog.dto.UserCreateDTO;
import com.devnoir.blog.dto.UserDTO;
import com.devnoir.blog.dto.UserProfileDTO;
import com.devnoir.blog.dto.UserRoleDTO;
import com.devnoir.blog.dto.UserUpdateDTO;
import com.devnoir.blog.entities.Role;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.projections.UserDetailsProjection;
import com.devnoir.blog.repositories.RoleRepository;
import com.devnoir.blog.repositories.UserRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.DatabaseException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@Service
public class AdminUserService implements UserDetailsService {

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
 		User user = optional.orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id)); 
 		return new UserDTO(user);
 	}
 	
 	@Transactional(readOnly = true) 
 	public UserDTO findByUsername(String username) {
 		Optional<User> optional = userRepository.findByUsername(username);
 		User user = optional.orElseThrow(() -> new ResourceNotFoundException("Username not found: " + username)); 
 		return new UserDTO(user);
 	}
 	
 	@Transactional
 	public UserDTO create(UserCreateDTO dto) {
 		User user = new User();
 		copyDtoToEntity(dto, user);
 		user.setPassword(passwordEncoder.encode(dto.getPassword()));
 		if (user.getRoles().isEmpty()) {
 			Role role = roleRepository.findByAuthority("ROLE_ADMIN")
 					.orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_ADMIN"));
 			user.getRoles().add(role);
 		}
 		user = userRepository.save(user);
 		return new UserDTO(user);
 	}
 	
 	@Transactional
 	public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
        copyDtoToEntity(dto, user);
        user = userRepository.save(user);
        return new UserDTO(user);
 	}
 	
 	@Transactional
    public UserDTO updateRole(Long id, UserRoleDTO dto) {
    	User user = userRepository.findById(id)
    	        .orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
        Role role = roleRepository.findByAuthority(dto.getRole())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + dto.getRole()));
        
        user.getRoles().clear();
        user.getRoles().add(role);
        user = userRepository.save(user);
        return new UserDTO(user);
    }
    
    @Transactional
    public UserDTO updateProfile(Long id, UserProfileDTO dto) {
    	User user = userRepository.findById(id)
    	        .orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
    	
    	if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
    		Optional<User> userWithSameEmail = userRepository.findByEmail(dto.getEmail());
    	    if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
    	        throw new BusinessException("Email already exists");
    	    }
    		user.setEmail(dto.getEmail());
    	}
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBio(dto.getBio());
        user.setImageUrl(dto.getImageUrl());
        user = userRepository.save(user);
        return new UserDTO(user);
    }
 	
	@Transactional
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
 		user.setBio(dto.getBio());
 		user.setImageUrl(dto.getImageUrl());
 		user.getRoles().clear();
 		for(RoleDTO roleDto : dto.getRoles()) {
 			Role role = roleRepository.findById(roleDto.getId())
 		            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleDto.getId()));
 			user.getRoles().add(role);
 		}
 	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetailsProjection> result = userRepository.searchUserAndRolesByUsername(username);
		if (result.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		
		return user;
	}
}