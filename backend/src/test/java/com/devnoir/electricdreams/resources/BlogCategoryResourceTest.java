package com.devnoir.electricdreams.resources;

import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogCategoryResourceTest {

	@Autowired
    private MockMvc mockMvc;

    // Caso de uso: Listar categorias por idioma (público)
    @Test
    void shouldListCategoriesByLanguage() throws Exception {
    	mockMvc.perform(get("/pt/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)));
    }
    
    @Test
    void shouldReturnEmptyListForInvalidLanguage() throws Exception {
        mockMvc.perform(get("/invalid/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
