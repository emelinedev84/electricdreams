package com.devnoir.electricdreams.dto;

import java.io.Serializable;

public class PostInsertDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String imageUrl;
	private PostContentDTO en;
	private PostContentDTO pt;
	
	public PostInsertDTO() {
	}

	public PostInsertDTO(String imageUrl) {
		this.imageUrl = imageUrl;
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