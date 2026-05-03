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
import com.devnoir.blog.dto.PostContentDTO;
import com.devnoir.blog.dto.PostCreateDTO;
import com.devnoir.blog.dto.PostDTO;
import com.devnoir.blog.dto.PostStatusDTO;
import com.devnoir.blog.dto.PostSummaryDTO;
import com.devnoir.blog.dto.TagDTO;
import com.devnoir.blog.enums.PostContentStatus;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.AdminPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminPostResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class AdminPostResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AdminPostService adminPostService;

	@Test
	@WithMockUser(roles = "ADMIN")
	void findAllSummaryShouldReturnPagedPosts() throws Exception {
		PostSummaryDTO dto = new PostSummaryDTO();
		dto.setId(1L);
		dto.setImageUrl("https://img.com/cover.jpg");
		dto.setAuthorId(1L);
		dto.setHasEN(true);
		dto.setTitleEN("Future of AI");
		dto.setStatusEN(PostContentStatus.PUBLISHED);
		dto.setHasPT(true);
		dto.setTitlePT("Futuro da IA");
		dto.setStatusPT(PostContentStatus.DRAFT);

		when(adminPostService.findAllSummary(any())).thenReturn(new PageImpl<>(List.of(dto)));

		mockMvc.perform(get("/api/admin/posts").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1)).andExpect(jsonPath("$.content[0].authorId").value(1))
				.andExpect(jsonPath("$.content[0].hasEN").value(true))
				.andExpect(jsonPath("$.content[0].titleEN").value("Future of AI"))
				.andExpect(jsonPath("$.content[0].statusEN").value("PUBLISHED"))
				.andExpect(jsonPath("$.content[0].hasPT").value(true))
				.andExpect(jsonPath("$.content[0].titlePT").value("Futuro da IA"))
				.andExpect(jsonPath("$.content[0].statusPT").value("DRAFT"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void findByIdShouldReturnCompletePostForAdminEditing() throws Exception {
		PostDTO dto = postDTO(true);

		when(adminPostService.findById(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/admin/posts/1").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.authorId").value(1))
				.andExpect(jsonPath("$.en.title").value("Future of AI"))
				.andExpect(jsonPath("$.pt.title").value("Futuro da IA"))
				.andExpect(jsonPath("$.categories[0].code").value("technology"))
				.andExpect(jsonPath("$.tags[0].code").value("ai"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createShouldReturn201AndLocationWhenPayloadIsValid() throws Exception {
		PostCreateDTO input = postCreateDTO(true);
		PostDTO output = postDTO(true);

		when(adminPostService.create(any(PostCreateDTO.class))).thenReturn(output);

		mockMvc.perform(post("/api/admin/posts").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/api/admin/posts/1"))
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.en.title").value("Future of AI"))
				.andExpect(jsonPath("$.pt.title").value("Futuro da IA"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createShouldReturn422WhenEnContentIsMissing() throws Exception {
		PostCreateDTO input = postCreateDTO(false);
		input.setEn(null);

		mockMvc.perform(post("/api/admin/posts").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation exception"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createShouldReturn422WhenCategoryIsMissing() throws Exception {
		PostCreateDTO input = postCreateDTO(false);
		input.getCategories().clear();

		mockMvc.perform(post("/api/admin/posts").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.errors[0].fieldName").value("categories"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateShouldReturnUpdatedPost() throws Exception {
		PostCreateDTO input = postCreateDTO(true);
		PostDTO output = postDTO(true);
		output.getEn().setTitle("Future of AI Updated");

		when(adminPostService.update(eq(1L), any(PostCreateDTO.class))).thenReturn(output);

		mockMvc.perform(put("/api/admin/posts/1").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.en.title").value("Future of AI Updated"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateStatusShouldReturnPostWithUpdatedStatus() throws Exception {
		PostStatusDTO input = new PostStatusDTO();
		input.setStatus(PostContentStatus.PUBLISHED);

		PostDTO output = postDTO(true);
		output.getEn().setStatus(PostContentStatus.PUBLISHED);
		output.getPt().setStatus(PostContentStatus.PUBLISHED);

		when(adminPostService.updateStatus(eq(1L), any(PostStatusDTO.class))).thenReturn(output);

		mockMvc.perform(put("/api/admin/posts/1/status").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.en.status").value("PUBLISHED"))
				.andExpect(jsonPath("$.pt.status").value("PUBLISHED"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateStatusShouldReturn422WhenStatusIsNull() throws Exception {
		PostStatusDTO input = new PostStatusDTO();

		mockMvc.perform(put("/api/admin/posts/1/status").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.errors[0].fieldName").value("status"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deletePostShouldReturn204() throws Exception {
		doNothing().when(adminPostService).deletePost(1L);

		mockMvc.perform(delete("/api/admin/posts/1").contextPath("/api")).andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deletePostContentShouldReturn204() throws Exception {
		doNothing().when(adminPostService).deletePostContent(1L, "PT");

		mockMvc.perform(delete("/api/admin/posts/1/content/PT").contextPath("/api")).andExpect(status().isNoContent());
	}

	private PostCreateDTO postCreateDTO(boolean includePt) {
		PostCreateDTO dto = new PostCreateDTO();
		dto.setAuthorId(1L);
		dto.setImageUrl("https://img.com/cover.jpg");
		dto.setEn(new PostContentDTO(null, "Future of AI", "English content",
				"This is a valid meta description for the English content.", PostContentStatus.DRAFT, null));

		if (includePt) {
			dto.setPt(new PostContentDTO(null, "Futuro da IA", "Conteudo em portugues",
					"Esta e uma meta description valida para o conteudo em portugues.", PostContentStatus.DRAFT, null));
		}

		dto.getCategories().add(new CategoryDTO(10L, "technology", "Technology", "Tecnologia"));
		dto.getTags().add(new TagDTO(20L, "ai", "AI", "IA"));
		return dto;
	}

	private PostDTO postDTO(boolean includePt) {
		PostDTO dto = new PostDTO();
		dto.setId(1L);
		dto.setAuthorId(1L);
		dto.setImageUrl("https://img.com/cover.jpg");
		dto.setEn(new PostContentDTO(100L, "Future of AI", "English content",
				"This is a valid meta description for the English content.", PostContentStatus.DRAFT, 1L));
		dto.getContents().add(dto.getEn());

		if (includePt) {
			dto.setPt(new PostContentDTO(101L, "Futuro da IA", "Conteudo em portugues",
					"Esta e uma meta description valida para o conteudo em portugues.", PostContentStatus.DRAFT, 1L));
			dto.getContents().add(dto.getPt());
		}

		dto.getCategories().add(new CategoryDTO(10L, "technology", "Technology", "Tecnologia"));
		dto.getTags().add(new TagDTO(20L, "ai", "AI", "IA"));
		return dto;
	}
}