package com.devnoir.electricdreams.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.dto.PostCreateDTO;
import com.devnoir.electricdreams.dto.PostDTO;
import com.devnoir.electricdreams.dto.PostSummaryDTO;
import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.repositories.PostRepository;
import com.devnoir.electricdreams.repositories.TagRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;
import com.devnoir.electricdreams.services.exceptions.DatabaseException;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@Service
public class AdminPostService {

	@Autowired
	private PostRepository postRepository;
 	
 	@Autowired
 	private TagRepository tagRepository;
 	
 	@Autowired
 	private UserRepository userRepository;
 	
 	@Autowired
 	private CategoryRepository categoryRepository;
 	
 	@Transactional(readOnly = true)
 	public Page<PostSummaryDTO> findAllSummary(Pageable pageable) {
 		Page<Post> page = postRepository.findAll(pageable);
 		return page.map(x -> new PostSummaryDTO(x));
 	}
 	
 	@Transactional(readOnly = true) 
 	public PostDTO findById(Long id) {
 	    Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found")); 
 	    
 	    return new PostDTO(post);
 	}
 	
 	@Transactional
 	public PostDTO create(PostCreateDTO dto) {
 	    validateCreateDTO(dto);
 	    
 	    Post post = new Post();
 	    post = postRepository.save(post); // Salvar primeiro para ter o ID
 	    post = createOrUpdatePostBasicInfo(post, dto);
 	    return new PostDTO(post);
 	}
 	
 	@Transactional
 	public PostDTO update(Long id, PostCreateDTO dto) {
 		validateCreateDTO(dto);
 		
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
		return new PostDTO(createOrUpdatePostBasicInfo(post, dto));
 	}
 	
 	@Transactional(propagation = Propagation.SUPPORTS)
 	public void deletePost(Long id) {
 		try {
 			Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
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
 	
 	private void validateCreateDTO(PostCreateDTO dto) {
 		if (dto.getAuthorId() == null) {
 	        throw new BusinessException("Author ID must not be null");
 	    }

 	    if (dto.getEn() == null && dto.getPt() == null) {
 	        throw new BusinessException("Post must have content in at least one language");
 	    }
 	}
 	
    private Post createOrUpdatePostBasicInfo(Post post, PostCreateDTO dto) {
    	// Primeiro instanciar o post se for null
        if (post == null) {
            post = new Post();
        }
        
        // Depois setar as informações básicas
        post.setImageUrl(dto.getImageUrl());
        User user = userRepository.getReferenceById(dto.getAuthorId());
        post.setAuthor(user);
        
        // Por fim atualizar o conteúdo
        if (dto.getEn() != null) {
            updateOrCreateContent(post, dto.getEn(), Language.EN);
        }
        if (dto.getPt() != null) {
            updateOrCreateContent(post, dto.getPt(), Language.PT);
        }
        
        return postRepository.save(post);
    }
 	
 	private void updateOrCreateContent(Post post, PostContentDTO contentDto, Language language) {
 	// Primeiro verifica se já existe conteúdo para este idioma
 	    boolean hasExistingContent = post.getContents().stream()
 	            .anyMatch(c -> c.getLanguage() == language);
 	            
 	    // Se estamos criando um novo conteúdo (não atualizando) e já existe um para este idioma
 	    PostContent content = post.getContents().stream()
 	            .filter(c -> c.getLanguage() == language)
 	            .findFirst()
 	            .orElse(new PostContent());
 	            
 	    if (hasExistingContent && content.getId() == null) {
 	        throw new BusinessException("Já existe conteúdo neste idioma");
 	    }
 		
 		content.setLanguage(language);
 		content.setUrlHandle(contentDto.getUrlHandle());
 		content.setTitle(contentDto.getTitle());
 		content.setContent(contentDto.getContent());
 		content.setMetaDescription(contentDto.getMetaDescription());
 		content.setIsDraft(contentDto.getIsDraft());
 		content.setPost(post);
 		
 		if (content.getTitle() == null || content.getTitle().trim().isEmpty()) {
 	        throw new BusinessException("Título é obrigatório para o idioma " + language);
 	    }
 	    
 	    // Validação do URL handle
 	    if (content.getUrlHandle() == null || content.getUrlHandle().trim().isEmpty()) {
 	        throw new BusinessException("URL handle é obrigatório para o idioma " + language);
 	    }
 	    
 	    // Validação de URL handle único
 	    Optional<Post> postWithSameHandle = postRepository.findByContentsUrlHandleAndContentsLanguage(
 	            content.getUrlHandle(), language);
 	    if (postWithSameHandle.isPresent() && !postWithSameHandle.get().getId().equals(post.getId())) {
 	        throw new BusinessException("URL handle já existe para o idioma " + language);
 	    }
 		
 		content.getTags().clear();
 		if (contentDto.getTags() != null) {
 			Set<String> tagNames = new HashSet<>();
 			for (TagDTO tagDto : contentDto.getTags()) {
 				if (!tagNames.add(tagDto.getName().toLowerCase())) {
 		            throw new BusinessException("Tag duplicada");
 		        }
 				Tag tag;
 				if (tagDto.isNew()) {
 					tag = new Tag();
 					tag.setName(tagDto.getName());
 					tag.setLanguage(Language.valueOf(tagDto.getLanguage()));
 					tag = tagRepository.save(tag);
 				} else {
 					tag = tagRepository.findById(tagDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagDto.getId()));
 				}
 				content.getTags().add(tag);
 			}
 		}
 		
 		content.getCategories().clear();
 	    if (contentDto.getCategories() == null || contentDto.getCategories().isEmpty()) {
 	        throw new BusinessException("Categoria é obrigatória para o idioma " + language);
 	    }
 		if (contentDto.getCategories() != null) {
 			for (CategoryDTO categoryDto : contentDto.getCategories()) {
 				Category category;
 				if (categoryDto.isNew()) {
 					category = new Category();
 					category.setName(categoryDto.getName());
 					category.setLanguage(Language.valueOf(categoryDto.getLanguage()));
 					category = categoryRepository.save(category);
 				} else {
 					category = categoryRepository.findById(categoryDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryDto.getId()));
 				}
 				content.getCategories().add(category);
 			}
 		}
 		
 		if (!post.getContents().contains(content)) {
 			post.getContents().add(content);
 		}
 	}
}
