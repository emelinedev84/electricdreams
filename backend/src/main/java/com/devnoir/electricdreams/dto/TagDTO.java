package com.devnoir.electricdreams.dto;

import java.io.Serializable;

import com.devnoir.electricdreams.entities.Tag;

public class TagDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String language;
	
	public TagDTO() {
	}

	public TagDTO(Long id, String name, String language) {
		this.id = id;
		this.name = name;
		this.language = language;
	}

	public TagDTO(Tag tag) {
		id = tag.getId();
		name = tag.getName();
		language = tag.getLanguage().name();
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
}
