package com.devnoir.blog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;

public class PostDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String imageUrl;
	private Instant createdAt;
	private Instant updatedAt;
	private Long authorId;
	private PostContentDTO en;
	private PostContentDTO pt;

	// Novos campos para conteúdo específico do idioma
	private String title;
	private String urlHandle;
	private String content;
	private String metaDescription;
	private PostContentStatus status;
	private Set<TagDTO> tags = new HashSet<>();
	private Set<CategoryDTO> categories = new HashSet<>();

	// Manter a lista de contents para compatibilidade
	private Set<PostContentDTO> contents = new HashSet<>();

	public PostDTO() {
	}

	public PostDTO(Post post) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		updatedAt = post.getUpdatedAt();
		authorId = post.getAuthor() != null ? post.getAuthor().getId() : null;

		if (post.getContents() != null) {
			post.getContents().forEach(postContent -> {
				PostContentDTO contentDto = new PostContentDTO(postContent);
				this.contents.add(contentDto);
				if (postContent.getLanguage() == Language.EN) {
					this.en = contentDto;
					this.title = postContent.getTitle();
					this.urlHandle = postContent.getUrlHandle();
					this.content = postContent.getContent();
					this.metaDescription = postContent.getMetaDescription();
					this.status = postContent.getStatus();
				}
				
		        if (postContent.getLanguage() == Language.PT) {
		            this.pt = contentDto;
		        }
			});
		}
		
		post.getTags().forEach(x -> this.tags.add(new TagDTO(x)));
		post.getCategories().forEach(x -> this.categories.add(new CategoryDTO(x)));
	}

	public PostDTO(Post post, Language language) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		createdAt = post.getCreatedAt();
		updatedAt = post.getUpdatedAt();
		authorId = post.getAuthor() != null ? post.getAuthor().getId() : null;

		post.getContents().stream().filter(postContent -> postContent.getLanguage() == language).findFirst()
				.ifPresent(langContent -> {
					this.title = langContent.getTitle();
					this.urlHandle = langContent.getUrlHandle();
					this.content = langContent.getContent();
					this.metaDescription = langContent.getMetaDescription();
					this.status = langContent.getStatus();
				});
		post.getTags().forEach(x -> this.tags.add(new TagDTO(x)));
		post.getCategories().forEach(x -> this.categories.add(new CategoryDTO(x)));
	}

	// Getters e setters existentes
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

	public PostContentDTO getEn() {
		return en;
	}

	public void setEn(PostContentDTO en) {
		this.en = en;
	}

	public PostContentDTO getPt() {
		return pt;
	}

	public void setPt(PostContentDTO pt) {
		this.pt = pt;
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

	public Set<TagDTO> getTags() {
		return tags;
	}

	public Set<CategoryDTO> getCategories() {
		return categories;
	}

	public Set<PostContentDTO> getContents() {
		return contents;
	}
}