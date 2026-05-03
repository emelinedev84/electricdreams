package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class PostTest {

	@Test
    void shouldCreatePostWithBasicFields() {
		User author = new User();
        author.setId(1L);
        author.setUsername("author");
		
		Post post = new Post();
        post.setId(1L);
        post.setImageUrl("image.jpg");
        post.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        post.setUpdatedAt(Instant.parse("2026-01-02T00:00:00Z"));
        post.setAuthor(author);
        
        Category category = new Category(1L, "books", "Books", "Livros");
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");
        PostContent content = new PostContent();
        content.setId(1L);
        content.setPost(post);

        post.getCategories().add(category);
        post.getTags().add(tag);
        post.getContents().add(content);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getImageUrl()).isEqualTo("image.jpg");
        assertThat(post.getAuthor()).isEqualTo(author);
        assertThat(post.getCategories()).contains(category);
        assertThat(post.getTags()).contains(tag);
        assertThat(post.getContents()).contains(content);
    }
	
	@Test
    void shouldSetCreatedAtOnPrePersist() {
        Post post = new Post();

        post.prePersist();

        assertThat(post.getCreatedAt()).isNotNull();
        assertThat(post.getUpdatedAt()).isNull();
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {
        Post post = new Post();

        post.preUpdate();

        assertThat(post.getUpdatedAt()).isNotNull();
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
