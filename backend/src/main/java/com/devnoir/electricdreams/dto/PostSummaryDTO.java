package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.time.Instant;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;

public class PostSummaryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Long authorId;
	private Boolean hasEN;
	private Boolean hasPT;
	
	public PostSummaryDTO() {
	}

	public PostSummaryDTO(Long id, String imageUrl, Instant createdAt, Long authorId, Boolean hasEN, Boolean hasPT) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.authorId = authorId;
		this.hasEN = hasEN;
		this.hasPT = hasPT;
	}
	
	public PostSummaryDTO(Post post) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		authorId = post.getAuthor().getId();
		hasEN = post.getContents().stream().anyMatch(content -> content.getLanguage() == Language.EN);
		hasPT = post.getContents().stream().anyMatch(content -> content.getLanguage() == Language.PT);
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

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public Boolean getHasEN() {
		return hasEN;
	}

	public void setHasEN(Boolean hasEN) {
		this.hasEN = hasEN;
	}

	public Boolean getHasPT() {
		return hasPT;
	}

	public void setHasPT(Boolean hasPT) {
		this.hasPT = hasPT;
	}
}