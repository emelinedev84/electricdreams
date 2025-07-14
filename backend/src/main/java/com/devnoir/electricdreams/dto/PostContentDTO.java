package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.services.validation.PostContentValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@PostContentValid
public class PostContentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@NotNull(message = "Language must be specified")
	private String language;
	@NotBlank(message = "URL handle is required")
	private String urlHandle;
	@NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
	private String title;
	@NotBlank(message = "Content is required")
	private String content;
	@Size(min = 50, max = 160, message = "Meta description must be between 50 and 160 characters")
	private String metaDescription;
	private Long postId;
	private Boolean isDraft;
    
	private List<TagDTO> tags = new ArrayList<>();
	@NotEmpty(message = "At least one category is required")
	private Set<CategoryDTO> categories = new HashSet<>();
	
	public PostContentDTO() {
	}
	
	public PostContentDTO(Long id, String language, String urlHandle, String title, String content,
			String metaDescription, Boolean isDraft, Long postId) {
		this.id = id;
		this.language = language;
		this.urlHandle = urlHandle;
		this.title = title;
		this.content = content;
		this.metaDescription = metaDescription;
		this.isDraft = isDraft;
		this.postId = postId;
	}

	public PostContentDTO(PostContent postContent) {
		id = postContent.getId();
		language = postContent.getLanguage() != null ? postContent.getLanguage().name() : null;
		urlHandle = postContent.getUrlHandle();
		title = postContent.getTitle();
		content = postContent.getContent();
		metaDescription = postContent.getMetaDescription();
		isDraft = postContent.getIsDraft();
		postId = postContent.getPost().getId();
		postContent.getTags().forEach(x -> this.tags.add(new TagDTO(x)));
		postContent.getCategories().forEach(x -> this.categories.add(new CategoryDTO(x)));
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getUrlHandle() {
		return urlHandle;
	}
	public void setUrlHandle(String urlHandle) {
		this.urlHandle = urlHandle;
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
	public Boolean getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<TagDTO> getTags() {
		return tags;
	}
	public Set<CategoryDTO> getCategories() {
		return categories;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
}