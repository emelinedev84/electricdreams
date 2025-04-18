package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class PostTest {

	@Test
    void shouldCreatePostWithBasicFields() {
		User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setEmail("author@example.com");
		
		Post post = new Post();
        post.setId(1L);
        post.setImageUrl("https://example.com/image.jpg");
        post.setAuthor(author);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(post.getAuthor()).isEqualTo(author);
        assertThat(post.getAuthor().getUsername()).isEqualTo("author");
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        Post post1 = new Post();
        post1.setId(1L);

        Post post2 = new Post();
        post2.setId(1L);

        assertThat(post1).isEqualTo(post2);
        assertThat(post1.hashCode()).isEqualTo(post2.hashCode());
    }

    @Test
    void shouldInitializeCollections() {
        Post post = new Post();
        
        assertThat(post.getContents()).isNotNull();
        assertThat(post.getContents()).isEmpty();
    }
}
