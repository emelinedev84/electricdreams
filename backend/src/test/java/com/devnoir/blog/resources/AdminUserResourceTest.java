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

import com.devnoir.blog.dto.RoleDTO;
import com.devnoir.blog.dto.UserCreateDTO;
import com.devnoir.blog.dto.UserDTO;
import com.devnoir.blog.dto.UserProfileDTO;
import com.devnoir.blog.dto.UserRoleDTO;
import com.devnoir.blog.dto.UserUpdateDTO;
import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.services.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminUserResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
public class AdminUserResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AdminUserService adminUserService;

	@Test
	@WithMockUser(roles = "ADMIN")
	void findAllShouldReturnPagedUsers() throws Exception {
		when(adminUserService.findAllPaged(any())).thenReturn(new PageImpl<>(List.of(userDTO())));

		mockMvc.perform(get("/api/admin/users").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].username").value("admin"))
				.andExpect(jsonPath("$.content[0].email").value("admin@email.com"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void findByIdShouldReturnUser() throws Exception {
		when(adminUserService.findById(1L)).thenReturn(userDTO());

		mockMvc.perform(get("/api/admin/users/1").contextPath("/api")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.username").value("admin"))
				.andExpect(jsonPath("$.roles[0].authority").value("ROLE_ADMIN"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void insertShouldReturn201AndLocation() throws Exception {
		UserCreateDTO input = userCreateDTO();

		when(adminUserService.create(any(UserCreateDTO.class))).thenReturn(userDTO());

		mockMvc.perform(post("/api/admin/users").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/api/admin/users/1"))
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.username").value("admin"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void insertShouldReturn422WhenPasswordIsWeak() throws Exception {
		UserCreateDTO input = userCreateDTO();
		input.setPassword("weak");

		mockMvc.perform(post("/api/admin/users").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.errors[0].fieldName").value("password"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateShouldReturnUpdatedUser() throws Exception {
		UserUpdateDTO input = new UserUpdateDTO();
		input.setUsername("admin-updated");
		input.setFirstName("Admin");
		input.setLastName("Updated");
		input.setEmail("updated@email.com");
		input.setBio("Updated bio");
		input.setImageUrl("https://img.com/updated.jpg");
		input.getRoles().add(new RoleDTO(1L, "ROLE_ADMIN"));

		UserDTO output = userDTO();
		output.setUsername("admin-updated");
		output.setEmail("updated@email.com");

		when(adminUserService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(output);

		mockMvc.perform(put("/api/admin/users/1").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("admin-updated"))
				.andExpect(jsonPath("$.email").value("updated@email.com"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateRoleShouldReturnUserWithNewRole() throws Exception {
		UserRoleDTO input = new UserRoleDTO("ROLE_ADMIN");

		when(adminUserService.updateRole(eq(1L), any(UserRoleDTO.class))).thenReturn(userDTO());

		mockMvc.perform(put("/api/admin/users/1/role").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.roles[0].authority").value("ROLE_ADMIN"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateRoleShouldReturn422WhenRoleIsBlank() throws Exception {
		UserRoleDTO input = new UserRoleDTO("");

		mockMvc.perform(put("/api/admin/users/1/role").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422)).andExpect(jsonPath("$.errors[0].fieldName").value("role"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void updateProfileShouldReturnUpdatedProfileIncludingEmail() throws Exception {
		UserProfileDTO input = new UserProfileDTO("New", "Name", "New bio", "https://img.com/new.jpg", "new@email.com");

		UserDTO output = userDTO();
		output.setFirstName("New");
		output.setLastName("Name");
		output.setBio("New bio");
		output.setImageUrl("https://img.com/new.jpg");
		output.setEmail("new@email.com");

		when(adminUserService.updateProfile(eq(1L), any(UserProfileDTO.class))).thenReturn(output);

		mockMvc.perform(put("/api/admin/users/1/profile").contextPath("/api").contentType("application/json")
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("New")).andExpect(jsonPath("$.lastName").value("Name"))
				.andExpect(jsonPath("$.email").value("new@email.com"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deleteShouldReturn204() throws Exception {
		doNothing().when(adminUserService).delete(1L);

		mockMvc.perform(delete("/api/admin/users/1").contextPath("/api")).andExpect(status().isNoContent());
	}

	private UserCreateDTO userCreateDTO() {
		UserCreateDTO dto = new UserCreateDTO();
		dto.setUsername("admin");
		dto.setFirstName("Admin");
		dto.setLastName("User");
		dto.setEmail("admin@email.com");
		dto.setBio("Admin bio");
		dto.setImageUrl("https://img.com/admin.jpg");
		dto.setPassword("Admin@123");
		dto.getRoles().add(new RoleDTO(1L, "ROLE_ADMIN"));
		return dto;
	}

	private UserDTO userDTO() {
		UserDTO dto = new UserDTO();
		dto.setId(1L);
		dto.setUsername("admin");
		dto.setFirstName("Admin");
		dto.setLastName("User");
		dto.setEmail("admin@email.com");
		dto.setBio("Admin bio");
		dto.setImageUrl("https://img.com/admin.jpg");
		dto.getRoles().add(new RoleDTO(1L, "ROLE_ADMIN"));
		return dto;
	}
}
