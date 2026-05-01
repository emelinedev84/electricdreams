package com.devnoir.blog.dto;

import java.io.Serializable;

import com.devnoir.blog.entities.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	@NotBlank(message = "Category name is required")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
	private String code;
	private String nameEn;
	private String namePt;
	
	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String code, String nameEn, String namePt) {
		this.id = id;
		this.code = code;
		this.nameEn = nameEn;
		this.namePt = namePt;
	}

	public CategoryDTO(Category category) {
		id = category.getId();
		code = category.getCode();
		nameEn = category.getNameEn();
		namePt = category.getNamePt();
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
