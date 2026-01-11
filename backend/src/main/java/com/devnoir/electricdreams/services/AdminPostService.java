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
 	    Post post = postRepository.findByIdWithContentsTagsAndCategories(id).orElseThrow(() -> new ResourceNotFoundException("Post not found")); 
 	    return new PostDTO(post);
 	}
 	
 	@Transactional
 	public PostDTO create(PostCreateDTO dto) {
 		validatePostCreation(dto);
 	    Post post = new Post();
 	    post = createOrUpdatePostBasicInfo(post, dto);
 	    Post savedPost = postRepository.findByIdWithContentsTagsAndCategories(post.getId())
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
 	    return new PostDTO(savedPost);
 	}
 	
 	@Transactional
 	public PostDTO update(Long id, PostCreateDTO dto) {
 		validatePostUpdate(id, dto);
 	    Post post = postRepository.findById(id)
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
 	    Post updatedPost = createOrUpdatePostBasicInfo(post, dto);
 	    // Recarregar para garantir que tags e categories estão carregadas
 	    Post savedPost = postRepository.findByIdWithContentsTagsAndCategories(updatedPost.getId())
 	        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
 	    return new PostDTO(savedPost);
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
 	
 	private void validatePostCreation(PostCreateDTO dto) {
 	    // Validações específicas de criação
 		if (dto.getAuthorId() == null) {
 	        throw new BusinessException("Author ID must not be null");
 	    }
 		
 		// Verificar se o autor existe
        if (!userRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author not found: " + dto.getAuthorId());
        }
        
        // Validar que pelo menos um conteúdo tem categoria
        if (dto.getEn() != null && (dto.getEn().getCategories() == null || dto.getEn().getCategories().isEmpty())) {
            throw new BusinessException("Category is required for language EN");
        }
        
        if (dto.getPt() != null && (dto.getPt().getCategories() == null || dto.getPt().getCategories().isEmpty())) {
            throw new BusinessException("Category is required for language PT");
        }

		if (dto.getEn() == null && dto.getPt() == null) { throw new
			BusinessException("Post must have content in at least one language"); 
 		}
		
		// Validar título e handle obrigatórios
	    if (dto.getEn() != null) {
	        if (dto.getEn().getTitle() == null || dto.getEn().getTitle().trim().isEmpty()) {
	            throw new BusinessException("Title is required for language EN");
	        }
	        if (dto.getEn().getUrlHandle() == null || dto.getEn().getUrlHandle().trim().isEmpty()) {
	            throw new BusinessException("URL handle is required for language EN");
	        }
	        
	        // Validar URL handle único
	        if (postRepository.findByContentsUrlHandleAndContentsLanguage(dto.getEn().getUrlHandle(), Language.EN).isPresent()) {
	            throw new BusinessException("URL handle already exists for this language: EN");
	        }
	    }
	    
	    if (dto.getPt() != null) {
	        if (dto.getPt().getTitle() == null || dto.getPt().getTitle().trim().isEmpty()) {
	            throw new BusinessException("Title is required for language PT");
	        }
	        if (dto.getPt().getUrlHandle() == null || dto.getPt().getUrlHandle().trim().isEmpty()) {
	            throw new BusinessException("URL handle is required for language PT");
	        }
	        
	        // Validar URL handle único
	        if (postRepository.findByContentsUrlHandleAndContentsLanguage(dto.getPt().getUrlHandle(), Language.PT).isPresent()) {
	            throw new BusinessException("URL handle already exists for this language: PT");
	        }
	    }
 	}
 	
 	private void validatePostUpdate(Long id, PostCreateDTO dto) {
        // Validações específicas de atualização
        if (dto.getAuthorId() == null) {
            throw new BusinessException("Author ID must not be null");
        }
        
        // Verificar se o autor existe
        if (!userRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author not found: " + dto.getAuthorId());
        }
        
        // validação de URL handle único na atualização
        if (dto.getEn() != null && dto.getEn().getUrlHandle() != null) {
            Optional<Post> existingPost = postRepository.findByContentsUrlHandleAndContentsLanguage(
                dto.getEn().getUrlHandle(), Language.EN);
            if (existingPost.isPresent() && !existingPost.get().getId().equals(id)) {
                throw new BusinessException("URL handle already exists for this language: EN");
            }
        }
        
        if (dto.getPt() != null && dto.getPt().getUrlHandle() != null) {
            Optional<Post> existingPost = postRepository.findByContentsUrlHandleAndContentsLanguage(
                dto.getPt().getUrlHandle(), Language.PT);
            if (existingPost.isPresent() && !existingPost.get().getId().equals(id)) {
                throw new BusinessException("URL handle already exists for this language: PT");
            }
        }
    }
 	
    private Post createOrUpdatePostBasicInfo(Post post, PostCreateDTO dto) {
    	// Primeiro instanciar o post se for null
        if (post == null) {
            post = new Post();
        }
        
        // Depois setar as informações básicas
        post.setImageUrl(dto.getImageUrl());
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + dto.getAuthorId()));
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
        PostContent content = findOrCreateContent(post, language);
        updateContentBasicInfo(content, contentDto, post, language);
        updateContentTags(content, contentDto);
        updateContentCategories(content, contentDto, language);
        
        if (!post.getContents().contains(content)) {
            post.getContents().add(content);
        }
    }

    private PostContent findOrCreateContent(Post post, Language language) {
    	return post.getContents().stream()
    	        .filter(c -> c.getLanguage() == language)
    	        .findFirst()
    	        .orElseGet(() -> {
    	            PostContent newContent = new PostContent();
    	            newContent.setPost(post);
    	            newContent.setLanguage(language);
    	            return newContent;
    	        });
    }

    private void updateContentBasicInfo(PostContent content, PostContentDTO contentDto, Post post, Language language) {
        content.setLanguage(language);
        content.setUrlHandle(contentDto.getUrlHandle());
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setMetaDescription(contentDto.getMetaDescription());
        content.setIsDraft(contentDto.getIsDraft());
        content.setPost(post);
    }

    private void updateContentTags(PostContent content, PostContentDTO contentDto) {
        content.getTags().clear();
        if (contentDto.getTags() != null) {
            Set<String> tagNames = new HashSet<>();
            for (TagDTO tagDto : contentDto.getTags()) {
                validateAndAddTag(content, tagDto, tagNames);
            }
        }
    }

    private void validateAndAddTag(PostContent content, TagDTO tagDto, Set<String> tagNames) {
    	String tagNameLower = tagDto.getName().toLowerCase();
        if (!tagNames.add(tagNameLower)) {
            throw new BusinessException("Duplicate tag in this content: " + tagDto.getName());
        }
        
        Tag tag;
        if (tagDto.isNew()) {
            // Se é nova, verificar se já existe no mesmo idioma
            Language tagLanguage = Language.valueOf(tagDto.getLanguage());
            Optional<Tag> existingTag = tagRepository.findByNameAndLanguage(tagDto.getName(), tagLanguage);
            if (existingTag.isPresent()) {
                throw new BusinessException("Tag already exists in this language: " + tagDto.getName());
            }
            tag = createNewTag(tagDto);
        } else {
            tag = findExistingTag(tagDto);
        }
        content.getTags().add(tag);
    }

    private Tag createNewTag(TagDTO tagDto) {
        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        tag.setLanguage(Language.valueOf(tagDto.getLanguage()));
        return tagRepository.save(tag);
    }

    private Tag findExistingTag(TagDTO tagDto) {
        return tagRepository.findById(tagDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagDto.getId()));
    }

    private void updateContentCategories(PostContent content, PostContentDTO contentDto, Language language) {
        content.getCategories().clear();
        
        if (contentDto.getCategories() != null) {
            contentDto.getCategories().forEach(categoryDto -> 
                addCategory(content, categoryDto));
        }
    }

    private void addCategory(PostContent content, CategoryDTO categoryDto) {
        Category category = categoryDto.isNew() ? 
            createNewCategory(categoryDto) : 
            findExistingCategory(categoryDto);
        content.getCategories().add(category);
    }

    private Category createNewCategory(CategoryDTO categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setLanguage(Language.valueOf(categoryDto.getLanguage()));
        return categoryRepository.save(category);
    }

    private Category findExistingCategory(CategoryDTO categoryDto) {
        return categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryDto.getId()));
    }
}
