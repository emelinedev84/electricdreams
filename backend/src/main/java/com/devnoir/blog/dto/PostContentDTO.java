package com.devnoir.blog.dto;

import java.io.Serializable;

import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.enums.PostContentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostContentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
	private String title;
	@NotBlank(message = "Content is required")
	private String content;
	@Size(min = 50, max = 160, message = "Meta description must be between 50 and 160 characters")
	private String metaDescription;
	private Long postId;
	@NotNull
	private PostContentStatus status;
    
	public PostContentDTO() {
	}
	
	public PostContentDTO(Long id, String title, String content,
			String metaDescription, PostContentStatus status, Long postId) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.metaDescription = metaDescription;
		this.status = status;
		this.postId = postId;
	}

	public PostContentDTO(PostContent postContent) {
		id = postContent.getId();
		title = postContent.getTitle();
		content = postContent.getContent();
		metaDescription = postContent.getMetaDescription();
		status = postContent.getStatus() != null ? postContent.getStatus() : null;
		postId = postContent.getPost().getId();
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getMetaDescription() {
		return metaDescription;
	}
	
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	
	public PostContentStatus getStatus() {
		return status;
	}
	
	public void setStatus(PostContentStatus status) {
		this.status = status;
	}
	
	public Long getPostId() {
		return postId;
	}
	
	public void setPostId(Long postId) {
		this.postId = postId;
	}
}