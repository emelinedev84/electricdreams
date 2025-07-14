package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.services.validation.TagValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@TagValid
public class TagDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 30, message = "Tag name must be between 2 and 30 characters")
	private String name;
	@NotNull(message = "Language must be specified")
	private String language;
	private boolean isNew;
	
	public TagDTO() {
	}

	public TagDTO(Long id, String name, String language) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.isNew = false;
	}

	public TagDTO(Tag tag) {
		id = tag.getId();
		name = tag.getName();
		language = tag.getLanguage().name();
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