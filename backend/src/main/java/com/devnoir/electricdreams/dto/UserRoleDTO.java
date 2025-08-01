package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public class UserRoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Role is required")
	private String role;
		
	public UserRoleDTO() {
	}

	public UserRoleDTO(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}