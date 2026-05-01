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
        
        // Se houver algum conteúdo, usa o primeiro para os campos principais
        if (post.getContents() != null && !post.getContents().isEmpty()) {
	        post.getContents().stream()
	            .findFirst()
	            .ifPresent(firstContent -> {
	                this.title = firstContent.getTitle();
	                this.urlHandle = firstContent.getUrlHandle();
	                this.content = firstContent.getContent();
	                this.metaDescription = firstContent.getMetaDescription();
	                this.status = firstContent.getStatus();
	    });
        
        // Mantém todos os conteúdos na lista contents
        post.getContents().forEach(postContent -> this.contents.add(new PostContentDTO(postContent)));
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
        
        post.getContents().stream()
            .filter(postContent -> postContent.getLanguage() == language)
            .findFirst()
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

    // Novos getters e setters para os campos de conteúdo
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