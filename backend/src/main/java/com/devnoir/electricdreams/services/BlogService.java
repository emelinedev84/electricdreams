package com.devnoir.electricdreams.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.PostDTO;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.PostRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@Service
public class BlogService {

	@Autowired
 	private PostRepository postRepository;
 	
 	@Transactional(readOnly = true)
	public Page<PostDTO> findAllPublicPosts(String language, Pageable pageable) {
 		try {
            Language lang = Language.valueOf(language.toUpperCase());
            Page<Post> page = postRepository.findByContentsLanguageAndContentsIsDraftFalse(lang, pageable);
            return page.map(post -> new PostDTO(post, lang));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid language: " + language);
        }
	}

 	@Transactional(readOnly = true)
	public PostDTO findPostByUrlHandleAndLanguage(String urlHandle, String language) {
 		try {
            Language lang = Language.valueOf(language.toUpperCase());
            Post post = postRepository.findByContentsUrlHandleAndContentsLanguageAndContentsIsDraftFalse(urlHandle, lang)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            return new PostDTO(post, lang);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid language: " + language);
        }
	}
}