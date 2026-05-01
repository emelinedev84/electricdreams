package com.devnoir.blog.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.CategoryDTO;
import com.devnoir.blog.dto.PostContentDTO;
import com.devnoir.blog.dto.PostCreateDTO;
import com.devnoir.blog.dto.PostDTO;
import com.devnoir.blog.dto.PostStatusDTO;
import com.devnoir.blog.dto.PostSummaryDTO;
import com.devnoir.blog.dto.TagDTO;
import com.devnoir.blog.entities.Category;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.CategoryRepository;
import com.devnoir.blog.repositories.PostRepository;
import com.devnoir.blog.repositories.TagRepository;
import com.devnoir.blog.repositories.UserRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.DatabaseException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@Service
public class AdminPostService {

	@Autowired
	private PostRepository postRepository;
 	
 	@Autowired
 	private UserRepository userRepository;
 	
 	@Autowired
 	private TagRepository tagRepository;
 	
 	@Autowired
 	private CategoryRepository categoryRepository;
 	
 	@Autowired
 	private UrlHandleService urlHandleService;
 	
 	@Transactional(readOnly = true)
 	public Page<PostSummaryDTO> findAllSummary(Pageable pageable) {
 		Page<Post> page = postRepository.findAll(pageable);
 		return page.map(x -> new PostSummaryDTO(x));
 	}
 	
 	@Transactional(readOnly = true) 
 	public PostDTO findById(Long id) {
 	    Post post = postRepository.findByIdWithContentsTagsAndCategories(id).orElseThrow(() -> new ResourceNotFoundException("Post not found")); 
 	    return new PostDTO(post);
 	}
 	
 	@Transactional
 	public PostDTO create(PostCreateDTO dto) {
  	    Post post = new Post();
 	    
 	    post.setImageUrl(dto.getImageUrl());
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + dto.getAuthorId()));
        post.setAuthor(user);
       
        post.getContents().add(createContent(post, dto.getEn(), Language.EN));
        
        if (dto.getPt() != null) {
        	post.getContents().add(createContent(post, dto.getPt(), Language.PT));
        }        
        Set<TagDTO> tags = new HashSet<>();
        for (TagDTO tagDto : dto.getTags()) {
            if (!tags.add(tagDto)) {
                throw new BusinessException("Duplicate tag in this post: " + tagDto.getCode());
            }
            Tag tag = tagRepository.findById(tagDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagDto.getId()));
            post.getTags().add(tag);
        }
        
        Set<CategoryDTO> categories = new HashSet<>();
        for (CategoryDTO categoryDto : dto.getCategories()) {
            if (!categories.add(categoryDto)) {
                throw new BusinessException("Duplicate category in this post: " + categoryDto.getCode());
            }
            Category category = categoryRepository.findById(categoryDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryDto.getId()));
            post.getCategories().add(category);
        }
       
        postRepository.save(post);
 	    
 	    
 	    Post savedPost = postRepository.findByIdWithContentsTagsAndCategories(post.getId())
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
 	    return new PostDTO(savedPost);
 	}
 	
 	@Transactional
 	public PostDTO update(Long id, PostCreateDTO dto) {
		
 	    Post post = postRepository.findByIdWithContentsTagsAndCategories(id)
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));

 	    post.setImageUrl(dto.getImageUrl());
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + dto.getAuthorId()));
        post.setAuthor(user);
        
        PostContent enContent = getContentByLanguage(post, Language.EN);
      
        if (enContent == null) {
        	post.getContents().add(createContent(post, dto.getEn(), Language.EN));
        } else {
        	updateContent(enContent, dto.getEn());
        }
        
        PostContent ptContent = getContentByLanguage(post, Language.PT);
        
        if (dto.getPt() == null) {
        	if (ptContent != null) {
        		post.getContents().remove(ptContent);
        	}
        } else if (ptContent == null) {
        	post.getContents().add(createContent(post, dto.getPt(), Language.PT));
        } else {
        	updateContent(ptContent, dto.getPt());
        }
        
        updatePostCategories(post, dto);
        updatePostTags(post, dto);
      
        postRepository.save(post);

 	    Post updatedPost = postRepository.findByIdWithContentsTagsAndCategories(post.getId())
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
 	    return new PostDTO(updatedPost);
 	}
 	
 	@Transactional
 	public PostDTO updateStatus(Long id, PostStatusDTO dto) {
 		Post post = postRepository.findByIdWithContentsTagsAndCategories(id)
 				.orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
 		post.getContents().forEach(content -> content.setStatus(dto.getStatus()));
 		postRepository.save(post);
 		return new PostDTO(post);
 	}
 	
 	@Transactional(propagation = Propagation.SUPPORTS)
 	public void deletePost(Long id) {
 		try {
 			Post post = postRepository.findByIdWithContentsTagsAndCategories(id)
 					.orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
 			post.getTags().clear();
 			post.getCategories().clear();
 			postRepository.delete(post);
 		} catch (DataIntegrityViolationException e) {
 			throw new DatabaseException("Integrity violation");
 		}
 	}
 	
 	@Transactional(propagation = Propagation.SUPPORTS)
	public void deletePostContent(Long id, String language) {
 	    try {
 	        Post post = postRepository.findByIdWithContents(id)
 	            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
 	            
 	        Language lang = Language.valueOf(language.toUpperCase());
 	        
 	        boolean removed = post.getContents().removeIf(content -> content.getLanguage() == lang);
 	        
 	        if (!removed) {
 	            throw new ResourceNotFoundException("Content not found for language: " + language);
 	        }
 	        
 	        if (post.getContents().isEmpty()) {
 	            postRepository.delete(post);
 	        } else {
 	            postRepository.save(post);
 	        }
 	    } catch (DataIntegrityViolationException e) {
 	        throw new DatabaseException("Integrity violation");
 	    }
	}
 	 	
 	private void updatePostTags(Post post, PostCreateDTO dto) {
        post.getTags().clear();
        if (dto.getTags() != null) {
            Set<TagDTO> tags = new HashSet<>();
            for (TagDTO tagDto : dto.getTags()) {
                if (!tags.add(tagDto)) {
                    throw new BusinessException("Duplicate tag in this post: " + tagDto.getCode());
                }
                Tag tag = tagRepository.findById(tagDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagDto.getId()));
                post.getTags().add(tag);
            }
        }
    }

    private void updatePostCategories(Post post, PostCreateDTO dto) {
        post.getCategories().clear();
        if (dto.getCategories() != null) {
            Set<CategoryDTO> categories = new HashSet<>();
            for (CategoryDTO categoryDto : dto.getCategories()) {
                if (!categories.add(categoryDto)) {
                    throw new BusinessException("Duplicate category in this post: " + categoryDto.getCode());
                }
                Category category = categoryRepository.findById(categoryDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryDto.getId()));
                post.getCategories().add(category);
            }
        }
    }
    
    private PostContent createContent(Post post, PostContentDTO contentDto, Language language) {
    	PostContent content = new PostContent();
    	content.setPost(post);
    	content.setLanguage(language);
    	content.setUrlHandle(urlHandleService.generateUniqueHandle(contentDto.getTitle(), language));
    	content.setTitle(contentDto.getTitle());
    	content.setContent(contentDto.getContent());
    	content.setMetaDescription(contentDto.getMetaDescription());
    	content.setStatus(contentDto.getStatus());
    	return content;
    }
    
    private PostContent getContentByLanguage(Post post, Language language) {
    	return post.getContents().stream()
    			.filter(content -> content.getLanguage() == language)
    			.findFirst()
    			.orElse(null);
    }
    
    private void updateContent(PostContent content, PostContentDTO dto) {
    	content.setTitle(dto.getTitle());
    	content.setContent(dto.getContent());
    	content.setMetaDescription(dto.getMetaDescription());
    	content.setStatus(dto.getStatus());
    }
}