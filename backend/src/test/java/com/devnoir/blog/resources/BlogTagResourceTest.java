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

import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.dto.TagPublicDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.BlogTagService;

@WebMvcTest(BlogTagResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class BlogTagResourceTest {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlogTagService tagService;

    @Test
    void findAllShouldReturnTagsLocalizedInEnglish() throws Exception {
        when(tagService.findAll(Language.EN))
                .thenReturn(List.of(new TagPublicDTO("ai", "AI")));

        mockMvc.perform(get("/api/EN/tags")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ai"))
                .andExpect(jsonPath("$[0].name").value("AI"));
    }

    @Test
    void findAllShouldReturnTagsLocalizedInPortuguese() throws Exception {
        when(tagService.findAll(Language.PT))
                .thenReturn(List.of(new TagPublicDTO("ai", "IA")));

        mockMvc.perform(get("/api/PT/tags")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ai"))
                .andExpect(jsonPath("$[0].name").value("IA"));
    }

    @Test
    void findPostsByTagShouldReturnPublishedPostsOnlyInRequestedLanguage() throws Exception {
        PublicPostSummaryDTO dto = new PublicPostSummaryDTO();
        dto.setId(1L);
        dto.setImageUrl("https://img.com/cover.jpg");
        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        dto.setTitle("Post em PT");
        dto.setUrlHandle("post-em-pt");
        dto.setLanguage(Language.PT);
        dto.getTags().add(new TagPublicDTO("ai", "IA"));

        when(tagService.findPostsByTag(eq("PT"), eq("ai"), any()))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/PT/tags/ai/posts")
                        .contextPath("/api")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Post em PT"))
                .andExpect(jsonPath("$.content[0].language").value("PT"))
                .andExpect(jsonPath("$.content[0].tags[0].name").value("IA"));
    }
}
