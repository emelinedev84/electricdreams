package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.electricdreams.entities.PostContent;

public class PostContentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String language;
	private String urlHandle;
	private String title;
	private String content;
	private String metaDescription;
	private Boolean isDraft;
	private Long postId;
    
	private Set<TagDTO> tags = new HashSet<>();
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
		language = postContent.getLanguage().name();
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
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Set<TagDTO> getTags() {
		return tags;
	}
	public Set<CategoryDTO> getCategories() {
		return categories;
	}
}