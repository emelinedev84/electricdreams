package com.devnoir.electricdreams.resources;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.hasSize;
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

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.dto.PostCreateDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.entities.Role;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.repositories.PostRepository;
import com.devnoir.electricdreams.repositories.RoleRepository;
import com.devnoir.electricdreams.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminPostResourceTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    private User testAuthor;
    private Category categoryPT;
    private Category categoryEN;
    private Post testPost;

	@BeforeEach
	void setup() {
	    // Clear existing data
	    postRepository.deleteAll();
	    userRepository.deleteAll();
	    categoryRepository.deleteAll();
	    roleRepository.deleteAll();
	    
	    // Create test user
	    testAuthor = new User();
	    testAuthor.setUsername("testAuthor");
	    testAuthor.setEmail("test@example.com");
        testAuthor.setPassword("password");
        
        Role writerRole = new Role();
        writerRole.setAuthority("ROLE_WRITER");
        writerRole = roleRepository.save(writerRole);
        
        // Now associate the saved role with the user
        testAuthor.getRoles().add(writerRole);
        testAuthor = userRepository.save(testAuthor);
	    
        // Create test category for PT
        categoryPT = new Category();
        categoryPT.setName("Test Category PT");
        categoryPT.setLanguage(Language.PT);
        categoryPT = categoryRepository.save(categoryPT);
        
        // Create test category for EN
        categoryEN = new Category();
        categoryEN.setName("Test Category EN");
        categoryEN.setLanguage(Language.EN);
        categoryEN = categoryRepository.save(categoryEN);
        
        // Create post with content in PT
        testPost = new Post();
        testPost.setAuthor(testAuthor);
        
        // Add PT content
        PostContent contentPT = new PostContent();
        contentPT.setLanguage(Language.PT);
        contentPT.setTitle("Título em Português");
        contentPT.setUrlHandle("titulo-em-portugues-" + System.currentTimeMillis());
        contentPT.setContent("Conteúdo");
        contentPT.setIsDraft(false);
        contentPT.setPost(testPost);
        contentPT.getCategories().add(categoryPT);
        testPost.getContents().add(contentPT);
        
        // Add EN content
        PostContent contentEN = new PostContent();
        contentEN.setLanguage(Language.EN);
        contentEN.setTitle("English Title");
        contentEN.setUrlHandle("english-title-" + System.currentTimeMillis());
        contentEN.setContent("Content");
        contentEN.setIsDraft(false);
        contentEN.setPost(testPost);
        contentEN.getCategories().add(categoryEN);
        testPost.getContents().add(contentEN);
        
        testPost = postRepository.save(testPost);
	}
	
    // Caso de uso: Listar todos os posts (admin)
	@Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldListAllPostsWithLanguageInfo() throws Exception {
		mockMvc.perform(get("/admin/posts")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.content", isA(java.util.List.class)))
	            .andExpect(jsonPath("$.content[0].hasEN", is(true)))
	            .andExpect(jsonPath("$.content[0].hasPT", is(true)))
	            .andExpect(jsonPath("$.content[0].isDraftEN", is(false)))
	            .andExpect(jsonPath("$.content[0].isDraftPT", is(false)));
    }

    // Caso de uso: Criar post com tags e categorias
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreatePostWithCategoriesAndTags() throws Exception {
    	PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(testAuthor.getId());
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setLanguage("EN"); // Adicionar language obrigatório
        contentEN.setTitle("Test Post");
        contentEN.setUrlHandle("test-post");
        contentEN.setContent("Content");
        contentEN.setIsDraft(false);
        contentEN.setMetaDescription("This is a test post with at least 50 characters to meet the validation requirements"); // Adicionar metaDescription válido
        
        CategoryDTO category = new CategoryDTO();
        category.setId(categoryEN.getId());
        category.setName(categoryEN.getName());
        category.setLanguage(categoryEN.getLanguage().name());
        contentEN.getCategories().add(category);

        dto.setEn(contentEN);

        mockMvc.perform(post("/admin/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contents[0].title", is("Test Post")));
    }

    // Caso de uso: Editar post e adicionar segundo idioma
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAddSecondLanguageToPost() throws Exception {
    	// Create a new post with only PT content first
        Post newPost = new Post();
        newPost.setAuthor(testAuthor);
        
        PostContent contentPT = new PostContent();
        contentPT.setLanguage(Language.PT);
        contentPT.setTitle("Novo Post PT");
        contentPT.setUrlHandle("novo-post-pt-" + System.currentTimeMillis());
        contentPT.setContent("Conteúdo PT");
        contentPT.setIsDraft(false);
        contentPT.setPost(newPost);
        contentPT.getCategories().add(categoryPT);
        newPost.getContents().add(contentPT);
        
        newPost = postRepository.save(newPost);
        
        // Now try to add EN content
        PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(testAuthor.getId());
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setLanguage("EN"); // Adicionar language obrigatório
        contentEN.setTitle("New English Content");
        contentEN.setUrlHandle("new-english-content-" + System.currentTimeMillis());
        contentEN.setContent("English Content");
        contentEN.setIsDraft(false);
        contentEN.setMetaDescription("This is new English content with at least 50 characters to meet validation requirements"); // Adicionar metaDescription válido
        
        CategoryDTO category = new CategoryDTO();
        category.setId(categoryEN.getId());  // Usando categoria EN para conteúdo EN
        category.setName(categoryEN.getName());
        category.setLanguage(categoryEN.getLanguage().name());
        contentEN.getCategories().add(category);

        dto.setEn(contentEN);

        mockMvc.perform(put("/admin/posts/{id}", newPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents", hasSize(2)))
                .andExpect(jsonPath("$.contents[*].language", hasItems("PT", "EN")));
    }

    // Caso de uso: Editar post existente
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldEditExistingPost() throws Exception {
    	PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(testAuthor.getId());
        
        PostContentDTO contentEN = new PostContentDTO();
        contentEN.setLanguage("EN"); // Adicionar language obrigatório
        contentEN.setTitle("Updated Title");
        contentEN.setUrlHandle("updated-title-" + System.currentTimeMillis());
        contentEN.setContent("Updated content");
        contentEN.setIsDraft(false);
        contentEN.setMetaDescription("This is updated content with at least 50 characters to meet validation requirements"); // Adicionar metaDescription válido
        
        CategoryDTO category = new CategoryDTO();
        category.setId(categoryEN.getId());
        category.setName(categoryEN.getName());
        category.setLanguage(categoryEN.getLanguage().name());
        contentEN.getCategories().add(category);
        
        dto.setEn(contentEN);

        mockMvc.perform(put("/admin/posts/{id}", testPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[?(@.language=='EN')].title", hasItem("Updated Title")));
    }

    // Caso de uso: Excluir post completo
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeletePostCompletelyWhenNoLanguagesRemain() throws Exception {
		mockMvc.perform(delete("/admin/posts/{id}", testPost.getId())).andExpect(status().isNoContent());

		mockMvc.perform(get("/admin/posts/{id}", testPost.getId())).andExpect(status().isNotFound());
    }

    // Caso de uso: Writer só acessa seus próprios posts
	/*
	 * @Test
	 * 
	 * @WithMockUser("testAuthor") void shouldAllowWriterAccessToOwnPostsOnly()
	 * throws Exception { // First, try to access own post
	 * mockMvc.perform(get("/admin/posts/{id}", testPost.getId()))
	 * .andExpect(status().isOk());
	 * 
	 * // Create another post with a different author User otherAuthor = new User();
	 * otherAuthor.setUsername("otherAuthor");
	 * otherAuthor.setEmail("other@example.com");
	 * otherAuthor.setPassword("password"); otherAuthor =
	 * userRepository.save(otherAuthor);
	 * 
	 * Post otherPost = new Post(); otherPost.setAuthor(otherAuthor);
	 * 
	 * PostContent content = new PostContent(); content.setLanguage(Language.PT);
	 * content.setTitle("Other Post"); content.setUrlHandle("other-post");
	 * content.setContent("Content"); content.setIsDraft(true);
	 * content.setPost(otherPost); content.getCategories().add(categoryPT);
	 * otherPost.getContents().add(content);
	 * 
	 * otherPost = postRepository.save(otherPost);
	 * 
	 * // Try to access other author's post mockMvc.perform(get("/admin/posts/{id}",
	 * otherPost.getId())) .andExpect(status().isForbidden()); }
	 */
}