package com.devnoir.blog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.enums.Language;

public class PublicPostSummaryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Instant updatedAt;
	private String title;
	private String urlHandle;
	private String metaDescription;
	private Language language;
	private Language fallbackLanguage;
	private Set<CategoryPublicDTO> categories = new HashSet<>();
	private Set<TagPublicDTO> tags = new HashSet<>();
	
	public PublicPostSummaryDTO() {
	}

	public PublicPostSummaryDTO(Post post, PostContent content, Language requestedLanguage) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		updatedAt = post.getUpdatedAt();
		title = content.getTitle();
		urlHandle = content.getUrlHandle();
		metaDescription = content.getMetaDescription();
		language = content.getLanguage();
		fallbackLanguage = content.getLanguage() == requestedLanguage ? null : content.getLanguage();
		
		post.getCategories().forEach(category -> categories.add(new CategoryPublicDTO(category.getCode(), category.getLocalizedName(requestedLanguage))));
		
		post.getTags().forEach(tag -> tags.add(new TagPublicDTO(tag.getCode(), tag.getLocalizedName(requestedLanguage))));
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrlHandle() {
		return urlHandle;
	}

	public void setUrlHandle(String urlHandle) {
		this.urlHandle = urlHandle;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Language getFallbackLanguage() {
		return fallbackLanguage;
	}

	public void setFallbackLanguage(Language fallbackLanguage) {
		this.fallbackLanguage = fallbackLanguage;
	}

	public Set<CategoryPublicDTO> getCategories() {
		return categories;
	}

	public Set<TagPublicDTO> getTags() {
		return tags;
	}
}
