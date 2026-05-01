package com.devnoir.blog.dto;

import java.io.Serializable;

import com.devnoir.blog.entities.Tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 30, message = "Tag name must be between 2 and 30 characters")
	private String code;
	private String nameEn;
	private String namePt;

	public TagDTO() {
	}

	public TagDTO(Long id, String code, String nameEn, String namePt) {
		this.id = id;
		this.code = code;
		this.nameEn = nameEn;
		this.namePt = namePt;
	}

	public TagDTO(Tag tag) {
		id = tag.getId();
		code = tag.getCode();
		nameEn = tag.getNameEn();
		namePt = tag.getNamePt();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNamePt() {
		return namePt;
	}

	public void setNamePt(String namePt) {
		this.namePt = namePt;
	}
}