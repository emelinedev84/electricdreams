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

import com.devnoir.blog.dto.TagDTO;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.AdminTagService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminTagResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class AdminTagResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AdminTagService adminTagService;

	@Test
	@WithMockUser(roles = "ADMIN")
	void findAllShouldReturnPagedTags() throws Exception {
		when(adminTagService.findAllPaged(any())).thenReturn(new PageImpl<>(List.of(new TagDTO(1L, "ai", "AI", "IA"))));

		mockMvc.perform(get("/api/admin/tags").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1)).andExpect(jsonPath("$.content[0].code").value("ai"))
				.andExpect(jsonPath("$.content[0].nameEn").value("AI"))
				.andExpect(jsonPath("$.content[0].namePt").value("IA"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void findByIdShouldReturnTag() throws Exception {
		when(adminTagService.findById(1L)).thenReturn(new TagDTO(1L, "ai", "AI", "IA"));

		mockMvc.perform(get("/api/admin/tags/1").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("ai"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createShouldReturn201AndLocation() throws Exception {
		TagDTO input = new TagDTO(null, "ai", "AI", "IA");
		TagDTO output = new TagDTO(1L, "ai", "AI", "IA");

		when(adminTagService.create(any(TagDTO.class))).thenReturn(output);

		mockMvc.perform(post("/api/admin/tags").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/api/admin/tags/1"))
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("ai"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createShouldReturn422WhenCodeIsBlank() throws Exception {
		TagDTO input = new TagDTO(null, "", "AI", "IA");

		mockMvc.perform(post("/api/admin/tags").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422)).andExpect(jsonPath("$.error").value("Validation exception"))
				.andExpect(jsonPath("$.errors[0].fieldName").value("code"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateShouldReturnUpdatedTag() throws Exception {
		TagDTO input = new TagDTO(null, "spring", "Spring", "Spring");
		TagDTO output = new TagDTO(1L, "spring", "Spring", "Spring");

		when(adminTagService.update(eq(1L), any(TagDTO.class))).thenReturn(output);

		mockMvc.perform(put("/api/admin/tags/1").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("spring"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deleteShouldReturn204() throws Exception {
		doNothing().when(adminTagService).delete(1L);

		mockMvc.perform(delete("/api/admin/tags/1").contextPath("/api")).andExpect(status().isNoContent());
	}
}
