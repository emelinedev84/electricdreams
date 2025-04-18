package com.devnoir.electricdreams.resources;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.PostRepository;
import com.devnoir.electricdreams.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogResourceTest {

	@Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setup() {
    	
    	postRepository.deleteAll();
        userRepository.deleteAll();
        
        // Criar novo usuário com email único
        User author = new User();
        author.setUsername("testAuthor");
        author.setEmail("unique-email@test.com");
        author = userRepository.save(author);
        
        // Create test post
        Post post = new Post();
        post.setImageUrl("https://example.com/image.jpg");
        post.setAuthor(author);
        post = postRepository.save(post);
        
        // Add English content
        PostContent contentEN = new PostContent();
        contentEN.setLanguage(Language.EN);
        contentEN.setTitle("Test Post");
        contentEN.setUrlHandle("meu-post-teste");
        contentEN.setContent("Test content");
        contentEN.setIsDraft(false);
        contentEN.setPost(post);
        post.getContents().add(contentEN);
        
        // Add Portuguese content
        PostContent contentPT = new PostContent();
        contentPT.setLanguage(Language.PT);
        contentPT.setTitle("Post Completo");
        contentPT.setUrlHandle("post-completo");
        contentPT.setContent("Conteúdo de teste");
        contentPT.setIsDraft(false);
        contentPT.setPost(post);
        post.getContents().add(contentPT);
        
        postRepository.save(post);
    }

    // Caso de uso: Listar posts públicos por idioma
    @Test
    void shouldListPublicPostsByLanguage() throws Exception {
        mockMvc.perform(get("/pt")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.content[*].isDraft", everyItem(is(false))));
    }

    // Caso de uso: Buscar post por urlHandle e idioma
    @Test
    void shouldReturnPostByUrlHandleAndLanguageWhenExistsAndPublished() throws Exception {
    	// Create test post
        Post post = new Post();
        post.setImageUrl("https://example.com/image.jpg");
        post.setAuthor(userRepository.findByUsername("testAuthor").get());
        post = postRepository.save(post);
        
        // Add English content
        PostContent contentEN = new PostContent();
        contentEN.setLanguage(Language.EN);
        contentEN.setTitle("Test Post");
        contentEN.setUrlHandle("test-post");
        contentEN.setContent("Test content");
        contentEN.setIsDraft(false);
        contentEN.setPost(post);
        post.getContents().add(contentEN);
        
        postRepository.save(post);

        mockMvc.perform(get("/en/posts/test-post")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urlHandle", is("test-post")))
                .andExpect(jsonPath("$.isDraft", is(false)))
                .andExpect(jsonPath("$.title", is("Test Post")));
    }

    // Caso de uso: Exibir post completo com categorias e tags
    @Test
    void shouldDisplayPostWithAllDetails() throws Exception {
    	mockMvc.perform(get("/pt/posts/post-completo")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Post Completo")))
                .andExpect(jsonPath("$.content", is("Conteúdo de teste")))
                .andExpect(jsonPath("$.urlHandle", is("post-completo")));
    }

	/*
	 * // Caso de uso: Exibir tags públicas
	 * 
	 * @Test void shouldDisplayTagsForPublicPosts() throws Exception {
	 * mockMvc.perform(get("/blog/tags") .param("lang", "pt")
	 * .accept(MediaType.APPLICATION_JSON)) .andExpect(status().isOk())
	 * .andExpect(jsonPath("$", isA(java.util.List.class)))
	 * .andExpect(jsonPath("$[*].name", everyItem(notNullValue())))
	 * .andExpect(jsonPath("$[*].language", everyItem(is("PT")))); }
	 * 
	 * // Caso de uso: Filtrar posts por categoria
	 * 
	 * @Test void shouldFilterPostsByCategory() throws Exception { String category =
	 * "tecnologia";
	 * 
	 * mockMvc.perform(get("/blog/posts") .param("category", category)
	 * .param("lang", "pt") .accept(MediaType.APPLICATION_JSON))
	 * .andExpect(status().isOk()) .andExpect(jsonPath("$.content",
	 * isA(java.util.List.class)))
	 * .andExpect(jsonPath("$.content[*].categories[*].name", hasItem(category))); }
	 * 
	 * // Caso de uso: Filtrar posts por tag
	 * 
	 * @Test void shouldFilterPostsByTag() throws Exception { String tag = "java";
	 * 
	 * mockMvc.perform(get("/blog/posts") .param("tag", tag) .param("lang", "pt")
	 * .accept(MediaType.APPLICATION_JSON)) .andExpect(status().isOk())
	 * .andExpect(jsonPath("$.content", isA(java.util.List.class)))
	 * .andExpect(jsonPath("$.content[*].tags[*].name", hasItem(tag))); }
	 */
}
