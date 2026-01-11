package com.devnoir.electricdreams.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.devnoir.electricdreams.enums.Language;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_content", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"post_id", "language"}),
	    @UniqueConstraint(columnNames = {"url_handle", "language"})
	})
public class PostContent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private Language language;
    @Column(nullable = false)
	private String urlHandle;
	private String title;
	private String content;
	private String metaDescription;
	private Boolean isDraft;
	
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
	private Post post;
    
	@ManyToMany
	@JoinTable(name = "tb_content_tag", joinColumns = @JoinColumn(name = "content_id"), inverseJoinColumns = @JoinColumn(name = "tag_id")) 
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "tb_content_category", joinColumns = @JoinColumn(name = "content_id"), inverseJoinColumns = @JoinColumn(name = "category_id")) 
	private Set<Category> categories = new HashSet<>();
    
    public PostContent() {
    }

	public PostContent(Long id, Language language, String urlHandle, String title, String content,
			String metaDescription, Boolean isDraft, Post post) {
		this.id = id;
		this.language = language;
		this.urlHandle = urlHandle;
		this.title = title;
		this.content = content;
		this.metaDescription = metaDescription;
		this.isDraft = isDraft;
		this.post = post;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
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

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
	
	public Set<Tag> getTags() {
		return tags;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, language);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostContent other = (PostContent) obj;
		return Objects.equals(id, other.id) && language == other.language;
	}
}