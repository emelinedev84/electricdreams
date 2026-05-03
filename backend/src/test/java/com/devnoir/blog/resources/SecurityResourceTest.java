package com.devnoir.blog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.blog.config.ResourceServerConfig;
import com.devnoir.blog.dto.CategoryDTO;
import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.AdminCategoryService;
import com.devnoir.blog.services.BlogService;

@WebMvcTest({ AdminCategoryResource.class, BlogResource.class })
@AutoConfigureMockMvc
@Import({ ResourceServerConfig.class, ResourceExceptionHandler.class })
@TestPropertySource(properties = "cors.origins=http://localhost:3000")
public class SecurityResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminCategoryService adminCategoryService;

	@MockitoBean
	private BlogService blogService;

	@Test
	void publicRoutesShouldBeAccessibleWithoutAuthentication() throws Exception {
		when(blogService.findAllPublicPosts(eq("EN"), any())).thenReturn(new PageImpl<PublicPostSummaryDTO>(List.of()));

		mockMvc.perform(get("/api/EN/posts").contextPath("/api")).andExpect(status().isOk());
	}

	@Test
	void adminRoutesShouldRejectRequestWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/admin/categories").contextPath("/api")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminRoutesShouldAcceptAuthenticatedAdmin() throws Exception {
		when(adminCategoryService.findAllPaged(any()))
				.thenReturn(new PageImpl<>(List.of(new CategoryDTO(1L, "technology", "Technology", "Tecnologia"))));

		mockMvc.perform(get("/api/admin/categories").contextPath("/api")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))).andExpect(status().isOk());
	}

	@Test
	void adminRoutesShouldRejectAuthenticatedUserWithoutAdminRole() throws Exception {
		mockMvc.perform(get("/api/admin/categories").contextPath("/api")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))).andExpect(status().isForbidden());
	}
}
