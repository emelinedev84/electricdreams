package com.devnoir.blog.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.devnoir.blog.enums.Language;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_tag", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"code"})
	})
public class Tag implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;
	private String nameEn;
	private String namePt;
	
	@ManyToMany(mappedBy = "tags")
	private Set<Post> posts = new HashSet<>();
	
	public Tag() {
	}

	public Tag(Long id, String code, String nameEn, String namePt) {
		this.id = id;
		this.code = code;
		this.nameEn = nameEn;
		this.namePt = namePt;
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
	
	public Set<Post> getPosts() {
		return posts;
	}

	public String getLocalizedName(Language lang) {
	    return (lang == Language.PT) ? namePt : nameEn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		return Objects.equals(id, other.id);
	}
}