package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.services.validation.PostContentLanguageValid;

import jakarta.validation.constraints.NotNull;

@PostContentLanguageValid
public class PostCreateDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Author ID is required")
	private Long authorId;
	private String imageUrl;
	private PostContentDTO en;
	private PostContentDTO pt;
	
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
}