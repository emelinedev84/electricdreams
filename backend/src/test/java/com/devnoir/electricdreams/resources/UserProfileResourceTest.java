package com.devnoir.electricdreams.resources;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.electricdreams.dto.UserProfileDTO;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.PostRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileResourceTest {

	@Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @BeforeEach
    void setup() {
        // Primeiro limpa os posts para não violar a constraint de chave estrangeira
        postRepository.deleteAll();
        // Depois limpa os usuários
        userRepository.deleteAll();
        
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetOwnProfileWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/profile/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldUpdateOwnProfileSuccessfully() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        mockMvc.perform(put("/profile/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void shouldNotUpdateWithInvalidEmail() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail("invalid-email");

        mockMvc.perform(put("/profile/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }
}
