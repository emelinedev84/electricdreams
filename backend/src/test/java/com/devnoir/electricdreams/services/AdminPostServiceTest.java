package com.devnoir.electricdreams.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.dto.PostCreateDTO;
import com.devnoir.electricdreams.dto.PostDTO;
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
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminPostServiceTest {
	
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminPostService service;

    private User author;
    private PostCreateDTO validDto;
    private Category validCategory;
    private Tag validTag;
    private Post validPost;

    @BeforeEach
    void setup() {
    	 // Setup do autor
        author = new User();
        author.setId(1L);
        author.setUsername("testauthor");

        // Setup da categoria padrão
        validCategory = new Category();
        validCategory.setId(1L);
        validCategory.setName("Test Category");
        validCategory.setLanguage(Language.EN);

        // Setup da tag padrão
        validTag = new Tag();
        validTag.setId(1L);
        validTag.setName("Test Tag");
        validTag.setLanguage(Language.EN);

        // Setup do post padrão
        validPost = new Post();
        validPost.setId(1L);
        validPost.setAuthor(author);

        // Setup do DTO com conteúdo EN válido
        validDto = new PostCreateDTO();
        validDto.setAuthorId(1L);
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setTitle("Test Post");
        contentEN.setUrlHandle("test-post");
        contentEN.setContent("Test content");
        contentEN.setIsDraft(false);
        
        // Adicionar categoria padrão ao contentEN
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Test Category");
        categoryDTO.setLanguage("EN");
        contentEN.getCategories().add(categoryDTO);
        
        validDto.setEn(contentEN);

        // Configurar mocks padrões
        lenient().when(userRepository.getReferenceById(1L)).thenReturn(author);
        lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(validCategory));
        lenient().when(tagRepository.findById(1L)).thenReturn(Optional.of(validTag));
    }
	
	// Caso de uso: Buscar post por ID
    @Test
    void shouldGetPostByIdWhenExists() {
    	PostContent content = new PostContent();
        content.setTitle("Test Post");
        content.setLanguage(Language.EN);
        content.setPost(validPost);
        validPost.getContents().add(content);

        when(postRepository.findById(1L)).thenReturn(Optional.of(validPost));

        PostDTO result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    // Caso de uso: Criar post com ao menos um idioma
    @Test
    void shouldCreatePostWithOneLanguageMinimum() {
    	when(postRepository.save(any(Post.class))).thenReturn(validPost);

        PostDTO result = service.create(validDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    // Caso de uso: Validar urlHandle único por idioma
    @Test
    void shouldNotCreatePostWhenUrlHandleIsDuplicated() {
    	// Criar um post existente com o mesmo urlHandle
        Post existingPost = new Post();
        existingPost.setId(2L); // ID diferente do post de teste
        
        PostContent existingContent = new PostContent();
        existingContent.setLanguage(Language.EN);
        existingContent.setUrlHandle("test-post"); // Mesmo urlHandle do validDto
        existingContent.setPost(existingPost);
        existingPost.getContents().add(existingContent);

        // Configurar o mock para retornar o post existente quando procurar pelo urlHandle
        when(postRepository.findByContentsUrlHandleAndContentsLanguage("test-post", Language.EN))
            .thenReturn(Optional.of(existingPost));

        // Verificar se a exceção é lançada
        assertThatThrownBy(() -> service.create(validDto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("URL handle já existe para o idioma EN");
    }

    // Caso de uso: Conteúdo 1x por idioma
    @Test
    void shouldNotAllowMoreThanOneContentPerLanguage() {
    	PostContent existingContent = new PostContent();
        existingContent.setLanguage(Language.EN);
        existingContent.setTitle("Existing Title");
        existingContent.setUrlHandle("existing-handle");
        existingContent.getCategories().add(validCategory);
        validPost.getContents().add(existingContent);

        when(postRepository.findById(1L)).thenReturn(Optional.of(validPost));
       
        assertThatThrownBy(() -> service.update(1L, validDto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Já existe conteúdo neste idioma");
    }

    // Caso de uso: Categoria obrigatória por idioma
    @Test
    void shouldNotCreatePostIfNoCategoryProvided() {
    	PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(1L);
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setTitle("Test Post");
        contentEN.setUrlHandle("test-post");
        contentEN.setContent("Test content");
        contentEN.setIsDraft(false);
        dto.setEn(contentEN);

        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Categoria é obrigatória para o idioma EN");
    }

    // Caso de uso: Excluir apenas conteúdo de idioma
    @Test
    void shouldDeleteSpecificLanguageContent() {
    	PostContent contentEN = new PostContent();
        contentEN.setLanguage(Language.EN);
        validPost.getContents().add(contentEN);
        
        PostContent contentPT = new PostContent();
        contentPT.setLanguage(Language.PT);
        validPost.getContents().add(contentPT);

        when(postRepository.findByIdWithContents(1L)).thenReturn(Optional.of(validPost));
        when(postRepository.save(any(Post.class))).thenReturn(validPost);

        service.deletePostContent(1L, "EN");

        verify(postRepository).save(validPost);
        assertThat(validPost.getContents()).hasSize(1);
        assertThat(validPost.getContents().iterator().next().getLanguage()).isEqualTo(Language.PT);
    }

    // Caso de uso: Validar duplicidade de tags por idioma
    @Test
    void shouldNotAllowDuplicateTagsPerLanguage() {
    	TagDTO tag = new TagDTO(1L, "test", "EN");
        validDto.getEn().getTags().add(tag);
        validDto.getEn().getTags().add(tag);

        assertThatThrownBy(() -> service.create(validDto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Tag duplicada");
    }

    // Caso de uso: Adicionar nova tag dinamicamente
    @Test
    void shouldAddNewTagWhenIsNew() {
    	TagDTO newTag = new TagDTO(null, "newTag", "EN");
        newTag.setNew(true);
        validDto.getEn().getTags().add(newTag);
        
        when(tagRepository.save(any(Tag.class))).thenReturn(validTag);
        when(postRepository.save(any(Post.class))).thenReturn(validPost);

        PostDTO result = service.create(validDto);

        verify(tagRepository).save(any(Tag.class));
        assertThat(result).isNotNull();
    }

    // Caso de uso: Reutilizar tag existente
    @Test
    void shouldAssociateExistingTagById() {
    	TagDTO tagDto = new TagDTO(1L, "existing", "EN");
        validDto.getEn().getTags().add(tagDto);

        when(postRepository.save(any(Post.class))).thenReturn(validPost);

        PostDTO result = service.create(validDto);

        assertThat(result).isNotNull();
        verify(tagRepository).findById(1L);
    }

    // Caso de uso: Título e handle obrigatórios
    @Test
    void shouldNotAllowMissingTitleOrHandle() {
    	// Preparar DTO com categoria válida
        PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(1L);
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setContent("Test content");
        contentEN.setIsDraft(false);
        
        CategoryDTO category = new CategoryDTO();
        category.setId(1L);
        category.setName("Test Category");
        category.setLanguage("EN");
        contentEN.getCategories().add(category);
        
        // Teste para título vazio mas com handle válido
        contentEN.setTitle("");
        contentEN.setUrlHandle("valid-handle");
        dto.setEn(contentEN);
        
        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Título é obrigatório para o idioma EN");

        // Teste para handle vazio mas com título válido
        contentEN.setTitle("Valid Title");
        contentEN.setUrlHandle("");

        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("URL handle é obrigatório para o idioma EN");
    }
    
    @Test
    void shouldThrowNotFoundWhenPostDoesNotExist() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Post not found");
    }
}
