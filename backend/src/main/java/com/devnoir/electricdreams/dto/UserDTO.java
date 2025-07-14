package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.electricdreams.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;
	private String firstName;
	private String lastName;
	@Email(message = "Invalid email")
	@NotBlank(message = "Email is required")
	private String email;
	@Size(max = 500, message = "Bio must not exceed 500 characters")
	private String bio;
	private String imageUrl;
	
	private Set<RoleDTO> roles = new HashSet<>();
	
	public UserDTO() {
	}

	public UserDTO(Long id, String username, String firstName, String lastName, String email, String bio, String imageUrl) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.bio = bio;
		this.imageUrl = imageUrl;
	}
	
	public UserDTO(User user) {
		id = user.getId();
		username = user.getUsername();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		email = user.getEmail();
		bio = user.getBio();
		imageUrl = user.getImageUrl();
		user.getRoles().forEach(x -> this.roles.add(new RoleDTO(x)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Set<RoleDTO> getRoles() {
		return roles;
	}
}
