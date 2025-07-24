package com.devnoir.electricdreams.resources;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminCategoryResourceTest {

	@Autowired
    private MockMvc mockMvc;

	@Autowired
    private ObjectMapper objectMapper;
	
    // Caso de uso: Listar todas as categorias (admin)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldListAllCategoriesAdmin() throws Exception {
    	mockMvc.perform(get("/admin/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.totalElements").exists());
    }

    // Caso de uso: Criar nova categoria com idioma
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateCategoryWithLanguage() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Nova Categoria");
        dto.setLanguage("PT");

        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Nova Categoria")))
                .andExpect(jsonPath("$.language", is("PT")));
    }
         
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldNotCreateDuplicateCategory() throws Exception {
        // Criar primeira categoria
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Categoria Existente");
        dto.setLanguage("PT");

        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Tentar criar categoria duplicada
        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }
}
