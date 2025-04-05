package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.electricdreams.entities.Post;

public class BlogPostDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Instant updatedAt;
	private Long authorId;
	
	private Set<PostContentDTO> contents = new HashSet<>();
	
	public BlogPostDTO() {
	}

	public BlogPostDTO(Long id, String imageUrl, Instant createdAt, Instant updatedAt, Long authorId) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.authorId = authorId;
	}
	
	public BlogPostDTO(Post post) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		updatedAt = post.getUpdatedAt();
		authorId = post.getAuthor().getId();
		post.getContents().forEach(content -> contents.add(new PostContentDTO(content)));
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

	public Set<PostContentDTO> getContents() {
		return contents;
	}
}