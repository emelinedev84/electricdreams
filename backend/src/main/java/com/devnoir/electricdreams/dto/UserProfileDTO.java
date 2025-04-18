package com.devnoir.electricdreams.dto;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String bio;
	private String imageUrl;
    private String email;
	
	public UserProfileDTO() {
	}

    public UserProfileDTO(String firstName, String lastName, String bio, String imageUrl, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.email = email;
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
	
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}