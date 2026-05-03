package com.devnoir.blog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.blog.dto.CategoryDTO;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.AdminCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminCategoryResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class AdminCategoryResourceTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminCategoryService adminCategoryService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllShouldReturnPagedCategories() throws Exception {
        when(adminCategoryService.findAllPaged(any()))
                .thenReturn(new PageImpl<>(List.of(new CategoryDTO(1L, "technology", "Technology", "Tecnologia"))));

        mockMvc.perform(get("/api/admin/categories")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].code").value("technology"))
                .andExpect(jsonPath("$.content[0].nameEn").value("Technology"))
                .andExpect(jsonPath("$.content[0].namePt").value("Tecnologia"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByIdShouldReturnCategory() throws Exception {
        when(adminCategoryService.findById(1L))
                .thenReturn(new CategoryDTO(1L, "technology", "Technology", "Tecnologia"));

        mockMvc.perform(get("/api/admin/categories/1")
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("technology"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createShouldReturn201AndLocation() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "technology", "Technology", "Tecnologia");
        CategoryDTO output = new CategoryDTO(1L, "technology", "Technology", "Tecnologia");

        when(adminCategoryService.create(any(CategoryDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/admin/categories")
                        .contextPath("/api")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/categories/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("technology"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createShouldReturn422WhenNameEnIsBlank() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "technology", "", "Tecnologia");

        mockMvc.perform(post("/api/admin/categories")
                        .contextPath("/api")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation exception"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("nameEn"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateShouldReturnUpdatedCategory() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "cinema", "Cinema", "Cinema");
        CategoryDTO output = new CategoryDTO(1L, "cinema", "Cinema", "Cinema");

        when(adminCategoryService.update(eq(1L), any(CategoryDTO.class))).thenReturn(output);

        mockMvc.perform(put("/api/admin/categories/1")
                        .contextPath("/api")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("cinema"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteShouldReturn204() throws Exception {
        doNothing().when(adminCategoryService).delete(1L);

        mockMvc.perform(delete("/api/admin/categories/1")
                        .contextPath("/api"))
                .andExpect(status().isNoContent());
    }
}
