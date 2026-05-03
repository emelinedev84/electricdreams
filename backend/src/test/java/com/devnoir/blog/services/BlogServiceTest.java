package com.devnoir.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.devnoir.blog.dto.PublicPostDetailDTO;
import com.devnoir.blog.entities.Category;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;
import com.devnoir.blog.repositories.PostRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

	@Mock
    private PostRepository postRepository;

	@InjectMocks
    private BlogService blogService;

    @Test
    void shouldListOnlyENPublishedPostsWhenLanguageIsEN() {
        PageRequest pageable = PageRequest.of(0, 10);
        Post post = postWithContent(Language.EN, "english-post", "English Post", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostsByLanguage(Language.EN, pageable))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = blogService.findAllPublicPosts("EN", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLanguage()).isEqualTo(Language.EN);
        assertThat(result.getContent().get(0).getFallbackLanguage()).isNull();
    }

    @Test
    void shouldListOnlyPTPublishedPostsWhenLanguageIsPTWithoutFallback() {
        PageRequest pageable = PageRequest.of(0, 10);
        Post post = postWithContent(Language.PT, "post-pt", "Post PT", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostsByLanguage(Language.PT, pageable))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = blogService.findAllPublicPosts("PT", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLanguage()).isEqualTo(Language.PT);
        assertThat(result.getContent().get(0).getFallbackLanguage()).isNull();
    }

    @Test
    void shouldThrowBusinessExceptionWhenLanguageIsInvalidInList() {
        PageRequest pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> blogService.findAllPublicPosts("BR", pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid language");
    }

    @Test
    void shouldOpenENPostByUrlHandle() {
        Post post = postWithContent(Language.EN, "future-of-ai", "Future of AI", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostByUrlHandleAndLanguage("future-of-ai", Language.EN))
                .thenReturn(Optional.of(post));

        PublicPostDetailDTO result = blogService.findPostByUrlHandleAndLanguage("future-of-ai", "EN");

        assertThat(result.getLanguage()).isEqualTo(Language.EN);
        assertThat(result.getFallbackLanguage()).isNull();
        assertThat(result.getUrlHandle()).isEqualTo("future-of-ai");
    }

    @Test
    void shouldOpenPTPostByUrlHandle() {
        Post post = postWithContent(Language.PT, "post-pt", "Post PT", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostByUrlHandleAndLanguage("post-pt", Language.PT))
                .thenReturn(Optional.of(post));

        PublicPostDetailDTO result = blogService.findPostByUrlHandleAndLanguage("post-pt", "PT");

        assertThat(result.getLanguage()).isEqualTo(Language.PT);
        assertThat(result.getFallbackLanguage()).isNull();
        assertThat(result.getUrlHandle()).isEqualTo("post-pt");
    }

    @Test
    void shouldFallbackToENWhenOpeningPTDetailAndOnlyENExists() {
        Post post = postWithContent(Language.EN, "future-of-ai", "Future of AI", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostByUrlHandleAndLanguage("future-of-ai", Language.PT))
                .thenReturn(Optional.empty());

        when(postRepository.findPublicPostByUrlHandleAndLanguage("future-of-ai", Language.EN))
                .thenReturn(Optional.of(post));

        PublicPostDetailDTO result = blogService.findPostByUrlHandleAndLanguage("future-of-ai", "PT");

        assertThat(result.getLanguage()).isEqualTo(Language.EN);
        assertThat(result.getFallbackLanguage()).isEqualTo(Language.EN);
        assertThat(result.getUrlHandle()).isEqualTo("future-of-ai");
    }

    @Test
    void shouldThrowNotFoundWhenPostDoesNotExistInRequestedLanguageOrFallback() {
        when(postRepository.findPublicPostByUrlHandleAndLanguage("missing", Language.PT))
                .thenReturn(Optional.empty());

        when(postRepository.findPublicPostByUrlHandleAndLanguage("missing", Language.EN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.findPostByUrlHandleAndLanguage("missing", "PT"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    void shouldThrowNotFoundWhenOpeningENDraftPost() {
        when(postRepository.findPublicPostByUrlHandleAndLanguage("draft-post", Language.EN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.findPostByUrlHandleAndLanguage("draft-post", "EN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    void shouldListPostsByCategoryAndLanguageWithoutFallback() {
        PageRequest pageable = PageRequest.of(0, 10);
        Post post = postWithContent(Language.PT, "post-pt", "Post PT", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostsByCategoryCodeAndLanguage("books", Language.PT, pageable))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = blogService.findPublicPostsByCategory("PT", "books", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLanguage()).isEqualTo(Language.PT);
    }

    @Test
    void shouldListPostsByTagAndLanguageWithoutFallback() {
        PageRequest pageable = PageRequest.of(0, 10);
        Post post = postWithContent(Language.EN, "english-post", "English Post", PostContentStatus.PUBLISHED);

        when(postRepository.findPublicPostsByTagCodeAndLanguage("fiction", Language.EN, pageable))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = blogService.findPublicPostsByTag("EN", "fiction", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLanguage()).isEqualTo(Language.EN);
    }

    private Post postWithContent(Language language, String urlHandle, String title, PostContentStatus status) {
        Post post = new Post();
        post.setId(1L);
        post.setImageUrl("image.jpg");

        Category category = new Category(1L, "books", "Books", "Livros");
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");
        post.getCategories().add(category);
        post.getTags().add(tag);

        PostContent content = new PostContent();
        content.setPost(post);
        content.setLanguage(language);
        content.setUrlHandle(urlHandle);
        content.setTitle(title);
        content.setContent("Content");
        content.setMetaDescription("Meta description");
        content.setStatus(status);

        post.getContents().add(content);
        return post;
    }
}
