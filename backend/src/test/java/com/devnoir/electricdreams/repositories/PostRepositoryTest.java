package com.devnoir.electricdreams.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.User;

@DataJpaTest
public class PostRepositoryTest {

	@Autowired
    private PostRepository postRepository;
	
	@Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindPostById() {
    	User author = new User();
        author.setUsername("writer");
        author.setEmail("writer@example.com");
        author = userRepository.save(author);
    	
    	Post post = new Post();
        post.setImageUrl("https://example.com/banner.png");
        post.setAuthor(author);

        Post saved = postRepository.save(post);
        Optional<Post> found = postRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAuthor()).isEqualTo(author);
    }

    @Test
    void shouldDeletePost() {
    	User author = new User();
        author.setUsername("toDelete");
        author.setEmail("todelete@example.com");
        author = userRepository.save(author);
    	
    	Post post = new Post();
        post.setAuthor(author);

        Post saved = postRepository.save(post);
        postRepository.delete(saved);

        Optional<Post> result = postRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
