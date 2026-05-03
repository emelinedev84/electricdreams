package com.devnoir.blog.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.blog.dto.CategoryPublicDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.BlogCategoryService;

@WebMvcTest(BlogCategoryResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class BlogCategoryResourceTest {

	@Autowired
    private MockMvc mockMvc;

	@MockitoBean
    private BlogCategoryService categoryService;

    @Test
    void findAllShouldReturnCategoriesLocalizedInPortuguese() throws Exception {
        when(categoryService.findAll(Language.PT))
                .thenReturn(List.of(new CategoryPublicDTO("books", "Livros")));

        mockMvc.perform(get("/api/PT/categories").contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("books"))
                .andExpect(jsonPath("$[0].name").value("Livros"));
    }
    
    @Test
    void shouldReturnEmptyListForInvalidLanguage() throws Exception {
        mockMvc.perform(get("/invalid/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
