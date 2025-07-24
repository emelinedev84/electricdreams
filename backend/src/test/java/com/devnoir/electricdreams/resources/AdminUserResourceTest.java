package com.devnoir.electricdreams.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.devnoir.electricdreams.dto.RoleDTO;
import com.devnoir.electricdreams.dto.UserCreateDTO;
import com.devnoir.electricdreams.dto.UserRoleDTO;
import com.devnoir.electricdreams.entities.Role;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.repositories.RoleRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminUserResourceTest {

	@Autowired
    private MockMvc mockMvc;
	
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setup() {
        // Criar roles necessárias se não existirem
        if (roleRepository.findByAuthority("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setAuthority("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        
        if (roleRepository.findByAuthority("ROLE_WRITER").isEmpty()) {
        	Role writerRole = new Role();
        	writerRole.setId(2L);
        	writerRole.setAuthority("ROLE_WRITER");
        	roleRepository.save(writerRole);
        }
    }

    // Caso de uso: Listar todos os usuários (paginação)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldListAllUsersWithPagination() throws Exception {
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    // Caso de uso: Criar novo usuário com definição de role
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateNewUserWithSpecifiedRole() throws Exception {
    	// Primeiro garantir que a role WRITER existe
        Role writerRole = roleRepository.findByAuthority("ROLE_WRITER")
            .orElseGet(() -> {
                Role role = new Role();
                role.setAuthority("ROLE_WRITER");
                return roleRepository.save(role);
            });

        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("Password123!"); // Senha que atende aos requisitos
        dto.setFirstName("New");
        dto.setLastName("User");
        
        RoleDTO roleDto = new RoleDTO();
        roleDto.setId(writerRole.getId()); // Usar o ID da role que acabamos de criar/buscar
        dto.getRoles().add(roleDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    // Caso de uso: Editar dados e role do usuário
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateUserDataAndRole() throws Exception {
    	// Criar usuário de teste com email único
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("updatetest@example.com"); // Email único
        user.setPassword("Password123!"); // Senha que atende aos requisitos
        user.setFirstName("New");
        user.setLastName("User");
        
        // Usar a role WRITER que já foi criada no setup
        Role writerRole = roleRepository.findByAuthority("ROLE_WRITER")
            .orElseThrow(() -> new RuntimeException("Role WRITER not found"));
        user.getRoles().add(writerRole);
        user = userRepository.save(user);

        // Criar DTO para atualização com role ADMIN
        UserRoleDTO dto = new UserRoleDTO("ROLE_ADMIN");

        // Fazer a requisição de atualização
        mockMvc.perform(put("/users/{id}/role", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0].authority").value("ROLE_ADMIN"));
    }

    // Caso de uso: Excluir usuário
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteUserByIdWhenUserExists() throws Exception {
    	// Criar um usuário para deletar
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setUsername("userToDelete");
        createDto.setEmail("delete@example.com");
        createDto.setPassword("Password123!"); // Senha que atende aos requisitos
        createDto.setFirstName("User");
        createDto.setLastName("ToDelete");
        
        // Adicionar role WRITER
        RoleDTO roleDto = new RoleDTO();
        Role writerRole = roleRepository.findByAuthority("ROLE_WRITER")
            .orElseGet(() -> {
                Role role = new Role();
                role.setAuthority("ROLE_WRITER");
                return roleRepository.save(role);
            });
        roleDto.setId(writerRole.getId());
        createDto.getRoles().add(roleDto);

        // Criar o usuário
        String result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Extrair o ID do usuário criado
        Long userId = objectMapper.readTree(result).get("id").asLong();

        // Deletar o usuário
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Verificar se o usuário foi realmente deletado
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    // Caso de uso: Admin acessa tudo
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAllowAdminAccessToAllResources() throws Exception {
    	// Criar um usuário primeiro para poder testá-lo
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testaccess@example.com"); // Email único
        user.setPassword("password123");
        user = userRepository.save(user);

        // Testa acesso a listagem
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Testa acesso a usuário específico usando o ID do usuário criado
        mockMvc.perform(get("/users/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
