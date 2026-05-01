package com.devnoir.blog.dto;

import com.devnoir.blog.enums.PostContentStatus;

import jakarta.validation.constraints.NotNull;

public class PostStatusDTO {

	@NotNull(message = "Status is required")
	private PostContentStatus status;

	public PostContentStatus getStatus() {
		return status;
	}

	public void setStatus(PostContentStatus status) {
		this.status = status;
	}
}
