package com.devnoir.blog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.blog.dto.CategoryPublicDTO;
import com.devnoir.blog.dto.PublicPostDetailDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.dto.TagPublicDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.BlogService;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(BlogResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class BlogResourceTest {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlogService blogService;

    @Test
    void findAllPublicPostsShouldReturnPublishedPostsInRequestedLanguageWithoutFallback() throws Exception {
        PublicPostSummaryDTO dto = publicSummaryDTO(
                1L,
                "Post em PT",
                "post-em-pt",
                Language.PT,
                null,
                "Tecnologia",
                "IA");

        when(blogService.findAllPublicPosts(eq("PT"), any()))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/PT/posts")
                        .contextPath("/api")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Post em PT"))
                .andExpect(jsonPath("$.content[0].urlHandle").value("post-em-pt"))
                .andExpect(jsonPath("$.content[0].language").value("PT"))
                .andExpect(jsonPath("$.content[0].fallbackLanguage").doesNotExist())
                .andExpect(jsonPath("$.content[0].categories[0].name").value("Tecnologia"))
                .andExpect(jsonPath("$.content[0].tags[0].name").value("IA"));
    }

    @Test
    void findPostByUrlHandleAndLanguageShouldReturnPostDetailInRequestedLanguage() throws Exception {
        PublicPostDetailDTO dto = publicDetailDTO(
                1L,
                "English Post",
                "english-post",
                "Full English content",
                Language.EN,
                null,
                "Technology",
                "AI");

        when(blogService.findPostByUrlHandleAndLanguage("english-post", "EN"))
                .thenReturn(dto);

        mockMvc.perform(get("/api/EN/posts/english-post")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("English Post"))
                .andExpect(jsonPath("$.urlHandle").value("english-post"))
                .andExpect(jsonPath("$.content").value("Full English content"))
                .andExpect(jsonPath("$.language").value("EN"))
                .andExpect(jsonPath("$.fallbackLanguage").doesNotExist())
                .andExpect(jsonPath("$.categories[0].name").value("Technology"))
                .andExpect(jsonPath("$.tags[0].name").value("AI"));
    }

    @Test
    void findPostByUrlHandleAndLanguageShouldReturnFallbackEnWhenPtDetailDoesNotExistButEnExists() throws Exception {
        PublicPostDetailDTO dto = publicDetailDTO(
                1L,
                "English Post",
                "english-post",
                "Full English content",
                Language.EN,
                Language.EN,
                "Technology",
                "AI");

        when(blogService.findPostByUrlHandleAndLanguage("english-post", "PT"))
                .thenReturn(dto);

        mockMvc.perform(get("/api/PT/posts/english-post")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("English Post"))
                .andExpect(jsonPath("$.urlHandle").value("english-post"))
                .andExpect(jsonPath("$.language").value("EN"))
                .andExpect(jsonPath("$.fallbackLanguage").value("EN"))
                .andExpect(jsonPath("$.categories[0].name").value("Technology"))
                .andExpect(jsonPath("$.tags[0].name").value("AI"));
    }

    @Test
    void findPostByUrlHandleAndLanguageShouldReturn404WhenPublishedPostDoesNotExist() throws Exception {
        when(blogService.findPostByUrlHandleAndLanguage("missing-post", "PT"))
                .thenThrow(new ResourceNotFoundException("Published content not found"));

        mockMvc.perform(get("/api/PT/posts/missing-post")
                        .contextPath("/api"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Published content not found"))
                .andExpect(jsonPath("$.path").value("/api/PT/posts/missing-post"));
    }

    private PublicPostSummaryDTO publicSummaryDTO(
            Long id,
            String title,
            String urlHandle,
            Language language,
            Language fallbackLanguage,
            String categoryName,
            String tagName) {
        PublicPostSummaryDTO dto = new PublicPostSummaryDTO();
        dto.setId(id);
        dto.setImageUrl("https://img.com/cover.jpg");
        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        dto.setTitle(title);
        dto.setUrlHandle(urlHandle);
        dto.setMetaDescription("Meta description");
        dto.setLanguage(language);
        dto.setFallbackLanguage(fallbackLanguage);
        dto.getCategories().add(new CategoryPublicDTO("technology", categoryName));
        dto.getTags().add(new TagPublicDTO("ai", tagName));
        return dto;
    }

    private PublicPostDetailDTO publicDetailDTO(
            Long id,
            String title,
            String urlHandle,
            String content,
            Language language,
            Language fallbackLanguage,
            String categoryName,
            String tagName) {
        PublicPostDetailDTO dto = new PublicPostDetailDTO();
        dto.setId(id);
        dto.setImageUrl("https://img.com/cover.jpg");
        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        dto.setTitle(title);
        dto.setUrlHandle(urlHandle);
        dto.setMetaDescription("Meta description");
        dto.setLanguage(language);
        dto.setFallbackLanguage(fallbackLanguage);
        dto.setContent(content);
        dto.getCategories().add(new CategoryPublicDTO("technology", categoryName));
        dto.getTags().add(new TagPublicDTO("ai", tagName));
        return dto;
    }
}
