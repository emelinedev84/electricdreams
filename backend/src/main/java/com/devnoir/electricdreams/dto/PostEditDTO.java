package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;

public class PostEditDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String imageUrl;
	private PostContentDTO en;
	private PostContentDTO pt;
	
	public PostEditDTO() {
	}

	public PostEditDTO(Long id, String imageUrl) {
		this.id = id;
		this.imageUrl = imageUrl;
	}
	
	public PostEditDTO(Post post) {
		id = post.getId();
		imageUrl = post.getImageUrl();
		post.getContents().forEach(content -> {
            if (content.getLanguage() == Language.EN) {
                this.en = new PostContentDTO(content);
            } else if (content.getLanguage() == Language.PT) {
                this.pt = new PostContentDTO(content);
            }
        });
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