package com.devnoir.electricdreams.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devnoir.electricdreams.dto.PostDTO;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.PostRepository;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

	@Mock
    private PostRepository postRepository;

    @InjectMocks
    private BlogService service;
    
	// Caso de uso: Não exibir posts com isDraft = true
    @Test
    void shouldNotReturnDraftPostsInPublicView() {
    	// Arrange
    	User author = new User();
        author.setId(1L);
        author.setUsername("testauthor");

        Post post = new Post();
        post.setAuthor(author);
        
        PostContent content = new PostContent();
        content.setTitle("Published Post");
        content.setIsDraft(false);
        content.setLanguage(Language.EN);
        content.setPost(post); 
        post.getContents().add(content);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(post));
        
        when(postRepository.findByContentsLanguageAndContentsIsDraftFalse(Language.EN, pageable)).thenReturn(page);

        // Act
        Page<PostDTO> result = service.findAllPublicPosts("EN", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    // Caso de uso: Filtrar corretamente por idioma ativo
    @Test
    void shouldReturnEntitiesMatchingActiveLanguage() {
    	// Arrange
    	User author = new User();
        author.setId(1L);
        author.setUsername("testauthor");

        Post post = new Post();
        post.setAuthor(author);
        
        PostContent content = new PostContent();
        content.setTitle("Post em Português");
        content.setIsDraft(false);
        content.setLanguage(Language.PT);
        content.setPost(post); 
        post.getContents().add(content);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(post));
        
        when(postRepository.findByContentsLanguageAndContentsIsDraftFalse(Language.PT, pageable)).thenReturn(page);

        // Act
        Page<PostDTO> result = service.findAllPublicPosts("PT", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
    
}
