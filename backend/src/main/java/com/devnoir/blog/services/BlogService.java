package com.devnoir.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.PublicPostDetailDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;
import com.devnoir.blog.repositories.PostRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@Service
public class BlogService {

	@Autowired
 	private PostRepository postRepository;
	
	private Language parseLanguage(String language) {
		try {
			return Language.valueOf(language.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Invalid language. Supported values: EN, PT");
		}
	}
 	
 	@Transactional(readOnly = true)
	public Page<PublicPostSummaryDTO> findAllPublicPosts(String language, Pageable pageable) {
        Language requestedLanguage = parseLanguage(language);
        Page<Post> page = postRepository.findPublicPostsWithPublishedEn(pageable);
        return page.map(post -> {
        	PostContent content = resolvePublicContent(post, requestedLanguage);
        	return new PublicPostSummaryDTO(post, content, requestedLanguage);
        });
	}
 	
 	@Transactional(readOnly = true)
 	public Page<PublicPostSummaryDTO> findPublicPostsByCategory(String language, String categoryCode, Pageable pageable) {
 		Language requestedLanguage = parseLanguage(language);
 		Page<Post> page = postRepository.findPublicPostsByCategoryCode(categoryCode, pageable);
 		return page.map(post -> {
 			PostContent content = resolvePublicContent(post, requestedLanguage);
 			return new PublicPostSummaryDTO(post, content, requestedLanguage);
 		});
 	}
 	
 	@Transactional(readOnly = true)
 	public Page<PublicPostSummaryDTO> findPublicPostsByTag(String language, String tagCode, Pageable pageable) {
 		Language requestedLanguage = parseLanguage(language);
 		Page<Post> page = postRepository.findPublicPostsByTagCode(tagCode, pageable);
 		return page.map(post -> {
 			PostContent content = resolvePublicContent(post, requestedLanguage);
 			return new PublicPostSummaryDTO(post, content, requestedLanguage);
 		});
 	}

 	@Transactional(readOnly = true)
	public PublicPostDetailDTO findPostByUrlHandleAndLanguage(String urlHandle, String language) {
        Language requestedLanguage = parseLanguage(language);
        Post post = postRepository.findPublicPostByUrlHandleAndLanguage(urlHandle, requestedLanguage)
            .orElseGet(() -> {
            	if (requestedLanguage == Language.PT) {
            		return postRepository.findPublicPostByUrlHandleAndLanguage(urlHandle, Language.EN)
            				.orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            	}
            	throw new ResourceNotFoundException("Post not found");
            });
        PostContent content = resolvePublicContent(post, requestedLanguage);
        return new PublicPostDetailDTO(post, content, requestedLanguage);
	}
 	
 	private PostContent resolvePublicContent(Post post, Language requestedLanguage) {
 		if (requestedLanguage == Language.PT) {
 			return post.getContents().stream()
 					.filter(content -> content.getLanguage() == Language.PT)
 					.filter(content -> content.getStatus() == PostContentStatus.PUBLISHED)
 					.findFirst()
 					.orElseGet(() -> resolvePublishedEnContent(post));
 		}
 		return resolvePublishedEnContent(post);
 	}
 	
 	private PostContent resolvePublishedEnContent(Post post) {
 		return post.getContents().stream()
 				.filter(content -> content.getLanguage() == Language.EN)
 				.filter(content -> content.getStatus() == PostContentStatus.PUBLISHED)
 				.findFirst()
 				.orElseThrow(() -> new ResourceNotFoundException("Published EN content not found"));
 	}
}