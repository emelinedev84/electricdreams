package com.devnoir.electricdreams.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;

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
    private Boolean isDraft;
    private List<TagDTO> tags = new ArrayList<>();
    private Set<CategoryDTO> categories = new HashSet<>();
    
    // Manter a lista de contents para compatibilidade
    private List<PostContentDTO> contents = new ArrayList<>();
    
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
	                this.isDraft = firstContent.getIsDraft();
	                firstContent.getTags().forEach(tag -> this.tags.add(new TagDTO(tag)));
	                firstContent.getCategories().forEach(category -> this.categories.add(new CategoryDTO(category)));
	            });
        
        // Mantém todos os conteúdos na lista contents
        post.getContents().forEach(postContent -> this.contents.add(new PostContentDTO(postContent)));
        }
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
                this.isDraft = langContent.getIsDraft();
                langContent.getTags().forEach(tag -> this.tags.add(new TagDTO(tag)));
                langContent.getCategories().forEach(category -> this.categories.add(new CategoryDTO(category)));
            });
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

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public List<PostContentDTO> getContents() {
        return contents;
    }
}