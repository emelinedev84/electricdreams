package com.devnoir.electricdreams.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.repositories.PostRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceExceptionHandlerTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private PostRepository postRepository;

    // Caso de uso: 404 - Entidade não encontrada
    // Caso de uso: 400 - Conflito de banco de dados
	@Test
	void shouldReturn400WhenDeletingCategoryInUse() throws Exception {
	    // Create a category
	    Category category = new Category();
	    category.setName("Test Category");
	    category.setLanguage(Language.EN);
	    category = categoryRepository.save(category);
	    
	    // Create a post that uses this category
	    Post post = new Post();
	    PostContent content = new PostContent();
	    content.setLanguage(Language.EN);
	    content.setTitle("Test Post");
	    content.setUrlHandle("test-post");
	    content.getCategories().add(category);
	    content.setPost(post);
	    post.getContents().add(content);
	    postRepository.save(post);
	    
	    // Try to delete the category that is being used by a post
	    mockMvc.perform(delete("/admin/categories/" + category.getId())
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isBadRequest());
	}

    // Caso de uso: 422 - Regra de negócio violada
    @Test
    void shouldReturn422WhenBusinessRuleIsViolated() throws Exception {
        
        mockMvc.perform(get("/invalid/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
