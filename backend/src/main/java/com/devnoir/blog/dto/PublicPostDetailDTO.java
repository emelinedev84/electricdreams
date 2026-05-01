package com.devnoir.blog.dto;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.enums.Language;

public class PublicPostDetailDTO extends PublicPostSummaryDTO {

	private static final long serialVersionUID = 1L;

	private String content;
	
	public PublicPostDetailDTO() {
	}

	public PublicPostDetailDTO(Post post, PostContent postContent, Language requestedLanguage) {
		super(post, postContent, requestedLanguage);
		content = postContent.getContent();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
