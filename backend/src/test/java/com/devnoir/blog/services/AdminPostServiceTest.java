package com.devnoir.blog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
import com.devnoir.blog.enums.PostContentStatus;
import com.devnoir.blog.repositories.CategoryRepository;
import com.devnoir.blog.repositories.PostRepository;
import com.devnoir.blog.repositories.TagRepository;
import com.devnoir.blog.repositories.UserRepository;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminPostServiceTest {
	
	@InjectMocks
    private AdminPostService service;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UrlHandleService urlHandleService;

    private User author;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("admin");
        author.setEmail("admin@email.com");

        category = new Category();
        category.setId(10L);
        category.setCode("technology");
        category.setNameEn("Technology");
        category.setNamePt("Tecnologia");

        tag = new Tag();
        tag.setId(20L);
        tag.setCode("ai");
        tag.setNameEn("AI");
        tag.setNamePt("IA");
    }

    @Test
    void findAllSummaryShouldReturnPagedPostSummaryDTO() {
        Post post = postWithContents(true);

        when(postRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(java.util.List.of(post)));

        var result = service.findAllSummary(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        PostSummaryDTO dto = result.getContent().get(0);
        assertEquals(100L, dto.getId());
    }

    @Test
    void findByIdShouldReturnPostDTOWithEnAndPtContent() {
        Post post = postWithContents(true);

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post));

        PostDTO result = service.findById(100L);

        assertEquals(100L, result.getId());
        assertNotNull(result.getEn());
        assertNotNull(result.getPt());
        assertEquals(2, result.getContents().size());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.findByIdWithContentsTagsAndCategories(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void createShouldCreatePostWithRequiredEnContentAndCategory() {
        PostCreateDTO dto = validPostCreateDTO(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));
        when(urlHandleService.generateUniqueHandle("Future of AI", Language.EN))
                .thenReturn("future-of-ai");

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(100L);
            return post;
        });

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenAnswer(invocation -> Optional.of(savedPostFromCreate(dto, false)));

        PostDTO result = service.create(dto);

        assertEquals(100L, result.getId());
        assertNotNull(result.getEn());
        assertEquals("Future of AI", result.getEn().getTitle());
        assertEquals(1, result.getCategories().size());
        assertEquals(1, result.getTags().size());

        verify(urlHandleService).generateUniqueHandle("Future of AI", Language.EN);
        verify(urlHandleService, never()).generateUniqueHandle(eq("Futuro da IA"), eq(Language.PT));
    }

    @Test
    void createShouldCreatePostWithOptionalPtContentWhenProvided() {
        PostCreateDTO dto = validPostCreateDTO(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));
        when(urlHandleService.generateUniqueHandle("Future of AI", Language.EN))
                .thenReturn("future-of-ai");
        when(urlHandleService.generateUniqueHandle("Futuro da IA", Language.PT))
                .thenReturn("futuro-da-ia");

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(100L);
            return post;
        });

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenAnswer(invocation -> Optional.of(savedPostFromCreate(dto, true)));

        PostDTO result = service.create(dto);

        assertNotNull(result.getEn());
        assertNotNull(result.getPt());
        assertEquals("Future of AI", result.getEn().getTitle());
        assertEquals("Futuro da IA", result.getPt().getTitle());

        verify(urlHandleService).generateUniqueHandle("Future of AI", Language.EN);
        verify(urlHandleService).generateUniqueHandle("Futuro da IA", Language.PT);
    }

    @Test
    void createShouldThrowResourceNotFoundExceptionWhenAuthorDoesNotExist() {
        PostCreateDTO dto = validPostCreateDTO(false);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createShouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        PostCreateDTO dto = validPostCreateDTO(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(urlHandleService.generateUniqueHandle("Future of AI", Language.EN))
                .thenReturn("future-of-ai");
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createShouldThrowResourceNotFoundExceptionWhenTagDoesNotExist() {
        PostCreateDTO dto = validPostCreateDTO(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(urlHandleService.generateUniqueHandle("Future of AI", Language.EN))
                .thenReturn("future-of-ai");
        when(tagRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        verify(postRepository, never()).save(any());
    }

    @Test
    void updateShouldUpdateExistingContentsWithoutDuplicatingLanguage() {
        Post post = postWithContents(true);
        PostCreateDTO dto = validPostCreateDTO(true);
        dto.getEn().setTitle("Future of AI Updated");
        dto.getPt().setTitle("Futuro da IA Atualizado");

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post))
                .thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));
        when(urlHandleService.generateUniqueHandleForUpdate("Future of AI Updated", Language.EN, 1001L))
                .thenReturn("future-of-ai-updated");
        when(urlHandleService.generateUniqueHandleForUpdate("Futuro da IA Atualizado", Language.PT, 1002L))
                .thenReturn("futuro-da-ia-atualizado");

        PostDTO result = service.update(100L, dto);

        assertEquals(2, result.getContents().size());
        assertEquals("Future of AI Updated", result.getEn().getTitle());
        assertEquals("Futuro da IA Atualizado", result.getPt().getTitle());

        verify(urlHandleService).generateUniqueHandleForUpdate("Future of AI Updated", Language.EN, 1001L);
        verify(urlHandleService).generateUniqueHandleForUpdate("Futuro da IA Atualizado", Language.PT, 1002L);
    }

    @Test
    void updateShouldRemovePtContentWhenDtoPtIsNull() {
        Post post = postWithContents(true);
        PostCreateDTO dto = validPostCreateDTO(false);

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post))
                .thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));

        PostDTO result = service.update(100L, dto);

        assertNotNull(result.getEn());
        assertEquals(null, result.getPt());
        assertEquals(1, result.getContents().size());
        assertFalse(result.getContents().stream()
                .anyMatch(content -> "Futuro da IA".equals(content.getTitle())));
    }

    @Test
    void updateShouldAddPtContentWhenPostDoesNotHavePtYet() {
        Post post = postWithContents(false);
        PostCreateDTO dto = validPostCreateDTO(true);

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post))
                .thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(tagRepository.findById(20L)).thenReturn(Optional.of(tag));
        when(urlHandleService.generateUniqueHandle("Futuro da IA", Language.PT))
                .thenReturn("futuro-da-ia");

        PostDTO result = service.update(100L, dto);

        assertNotNull(result.getEn());
        assertNotNull(result.getPt());
        assertEquals(2, result.getContents().size());

        verify(urlHandleService).generateUniqueHandle("Futuro da IA", Language.PT);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenPostDoesNotExist() {
        PostCreateDTO dto = validPostCreateDTO(false);

        when(postRepository.findByIdWithContentsTagsAndCategories(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, dto));
    }

    @Test
    void updateStatusShouldApplySameStatusToAllContents() {
        Post post = postWithContents(true);
        PostStatusDTO dto = new PostStatusDTO();
        dto.setStatus(PostContentStatus.PUBLISHED);

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post));

        PostDTO result = service.updateStatus(100L, dto);

        assertEquals(PostContentStatus.PUBLISHED, result.getEn().getStatus());
        assertEquals(PostContentStatus.PUBLISHED, result.getPt().getStatus());
        verify(postRepository).save(post);
    }

    @Test
    void deletePostShouldClearRelationsAndDeletePost() {
        Post post = postWithContents(true);

        when(postRepository.findByIdWithContentsTagsAndCategories(100L))
                .thenReturn(Optional.of(post));

        service.deletePost(100L);

        assertEquals(0, post.getTags().size());
        assertEquals(0, post.getCategories().size());
        verify(postRepository).delete(post);
    }

    @Test
    void deletePostShouldThrowResourceNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.findByIdWithContentsTagsAndCategories(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deletePost(999L));
    }

    @Test
    void deletePostContentShouldRemoveOnlyRequestedLanguageAndSavePost() {
        Post post = postWithContents(true);

        when(postRepository.findByIdWithContents(100L))
                .thenReturn(Optional.of(post));

        service.deletePostContent(100L, "PT");

        assertEquals(1, post.getContents().size());
        assertEquals(Language.EN, post.getContents().iterator().next().getLanguage());
        verify(postRepository).save(post);
        verify(postRepository, never()).delete(post);
    }

    @Test
    void deletePostContentShouldDeletePostWhenLastContentIsRemoved() {
        Post post = postWithContents(false);

        when(postRepository.findByIdWithContents(100L))
                .thenReturn(Optional.of(post));

        service.deletePostContent(100L, "EN");

        assertEquals(0, post.getContents().size());
        verify(postRepository).delete(post);
        verify(postRepository, never()).save(post);
    }

    @Test
    void deletePostContentShouldThrowResourceNotFoundExceptionWhenLanguageContentDoesNotExist() {
        Post post = postWithContents(false);

        when(postRepository.findByIdWithContents(100L))
                .thenReturn(Optional.of(post));

        assertThrows(ResourceNotFoundException.class, () -> service.deletePostContent(100L, "PT"));
    }

    private PostCreateDTO validPostCreateDTO(boolean includePt) {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(1L);
        dto.setImageUrl("https://img.com/cover.jpg");
        dto.setEn(new PostContentDTO(
                null,
                "Future of AI",
                "English content",
                "This is a valid meta description for the English content.",
                PostContentStatus.DRAFT,
                null));

        if (includePt) {
            dto.setPt(new PostContentDTO(
                    null,
                    "Futuro da IA",
                    "Conteudo em portugues",
                    "Esta e uma meta description valida para o conteudo em portugues.",
                    PostContentStatus.DRAFT,
                    null));
        }

        dto.getCategories().add(new CategoryDTO(10L, "technology", "Technology", "Tecnologia"));
        dto.getTags().add(new TagDTO(20L, "ai", "AI", "IA"));
        return dto;
    }

    private Post postWithContents(boolean includePt) {
        Post post = new Post();
        post.setId(100L);
        post.setImageUrl("https://img.com/cover.jpg");
        post.setAuthor(author);
        post.getCategories().add(category);
        post.getTags().add(tag);

        PostContent en = new PostContent();
        en.setId(1001L);
        en.setPost(post);
        en.setLanguage(Language.EN);
        en.setTitle("Future of AI");
        en.setUrlHandle("future-of-ai");
        en.setContent("English content");
        en.setMetaDescription("This is a valid meta description for the English content.");
        en.setStatus(PostContentStatus.DRAFT);
        post.getContents().add(en);

        if (includePt) {
            PostContent pt = new PostContent();
            pt.setId(1002L);
            pt.setPost(post);
            pt.setLanguage(Language.PT);
            pt.setTitle("Futuro da IA");
            pt.setUrlHandle("futuro-da-ia");
            pt.setContent("Conteudo em portugues");
            pt.setMetaDescription("Esta e uma meta description valida para o conteudo em portugues.");
            pt.setStatus(PostContentStatus.DRAFT);
            post.getContents().add(pt);
        }

        return post;
    }

    private Post savedPostFromCreate(PostCreateDTO dto, boolean includePt) {
        Post post = postWithContents(includePt);
        post.getContents().forEach(content -> {
            if (content.getLanguage() == Language.EN) {
                content.setTitle(dto.getEn().getTitle());
                content.setUrlHandle("future-of-ai");
                content.setStatus(dto.getEn().getStatus());
            }
            if (content.getLanguage() == Language.PT) {
                content.setTitle(dto.getPt().getTitle());
                content.setUrlHandle("futuro-da-ia");
                content.setStatus(dto.getPt().getStatus());
            }
        });
        return post;
    }
}
