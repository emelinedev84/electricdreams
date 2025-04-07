package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.enums.Language;

public class PostSummaryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Long authorId;
	private Boolean hasEN;
	private Boolean hasPT;
	private Boolean isDraftEN;
	private Boolean isDraftPT;
	private String titleEN;
	private String titlePT;
	
	public PostSummaryDTO() {
	}

	public PostSummaryDTO(Long id, String imageUrl, Instant createdAt, Long authorId, Boolean hasEN, Boolean hasPT,
			Boolean isDraftEN, Boolean isDraftPT, String titleEN, String titlePT) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.authorId = authorId;
		this.hasEN = hasEN;
		this.hasPT = hasPT;
		this.isDraftEN = isDraftEN;
		this.isDraftPT = isDraftPT;
		this.titleEN = titleEN;
		this.titlePT = titlePT;
	}

	public PostSummaryDTO(Post post) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		authorId = post.getAuthor().getId();
		Optional<PostContent> enContent = post.getContents().stream()
				.filter(content -> content.getLanguage() == Language.EN).findFirst();
		hasEN = enContent.isPresent();
		isDraftEN = enContent.map(PostContent::getIsDraft).orElse(null);
		titleEN = enContent.map(PostContent::getTitle).orElse(null);

		Optional<PostContent> ptContent = post.getContents().stream()
				.filter(content -> content.getLanguage() == Language.PT).findFirst();
		hasPT = ptContent.isPresent();
		isDraftPT = ptContent.map(PostContent::getIsDraft).orElse(null);
		titlePT = ptContent.map(PostContent::getTitle).orElse(null);
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
	public Boolean getIsDraftEN() {
		return isDraftEN;
	}
	public void setIsDraftEN(Boolean isDraftEN) {
		this.isDraftEN = isDraftEN;
	}
	public Boolean getIsDraftPT() {
		return isDraftPT;
	}
	public void setIsDraftPT(Boolean isDraftPT) {
		this.isDraftPT = isDraftPT;
	}
	public String getTitleEN() {
		return titleEN;
	}
	public void setTitleEN(String titleEN) {
		this.titleEN = titleEN;
	}
	public String getTitlePT() {
		return titlePT;
	}
	public void setTitlePT(String titlePT) {
		this.titlePT = titlePT;
	}
}