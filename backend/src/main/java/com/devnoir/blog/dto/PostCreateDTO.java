package com.devnoir.blog.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.services.validation.PostContentLanguageValid;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@PostContentLanguageValid
public class PostCreateDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Author ID is required")
	private Long authorId;
	private String imageUrl;
	private PostContentDTO en;
	private PostContentDTO pt;
	
	private Set<TagDTO> tags = new HashSet<>();
	
	@NotEmpty(message = "At least one category is required")
	private Set<CategoryDTO> categories = new HashSet<>();
	
	public PostCreateDTO() {
	}

	public PostCreateDTO(Long authorId, String imageUrl) {
		this.authorId = authorId;
		this.imageUrl = imageUrl;
	}
	
	public PostCreateDTO(Post post) {
		authorId = post.getAuthor().getId();
		imageUrl = post.getImageUrl();
		en = post.getContents().stream().filter(content -> content.getLanguage() == Language.EN).findFirst()
				.map(x -> new PostContentDTO(x)).orElse(null);
		pt = post.getContents().stream().filter(content -> content.getLanguage() == Language.PT).findFirst()
				.map(x -> new PostContentDTO(x)).orElse(null);
		post.getTags().forEach(x -> this.tags.add(new TagDTO(x)));
		post.getCategories().forEach(x -> this.categories.add(new CategoryDTO(x)));
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	public Set<TagDTO> getTags() {
		return tags;
	}

	public Set<CategoryDTO> getCategories() {
		return categories;
	}
	
}