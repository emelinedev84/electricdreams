package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.time.Instant;

public class PostDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Instant updatedAt;
	private Long authorId;
	
	public PostDTO() {
	}

	public PostDTO(Long id, String imageUrl, Instant createdAt, Instant updatedAt, Long authorId) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.authorId = authorId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}	
}