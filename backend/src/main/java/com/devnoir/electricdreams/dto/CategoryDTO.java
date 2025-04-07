package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import com.devnoir.electricdreams.entities.Category;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String language;
	private boolean isNew;
	
	public CategoryDTO() {
	}
	
	public CategoryDTO(Long id, String name, String language) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.isNew = false;
	}

	public CategoryDTO(Category category) {
		id = category.getId();
		name = category.getName();
		language = category.getLanguage().name();
		this.isNew = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
}
