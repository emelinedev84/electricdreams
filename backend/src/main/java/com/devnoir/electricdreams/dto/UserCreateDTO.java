package com.devnoir.electricdreams.dto;

public class UserCreateDTO extends UserDTO {

	private static final long serialVersionUID = 1L;
	
	private String password;
	
	public UserCreateDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
