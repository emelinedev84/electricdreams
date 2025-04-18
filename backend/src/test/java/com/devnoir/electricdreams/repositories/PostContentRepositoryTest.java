package com.devnoir.electricdreams.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.enums.Language;

@DataJpaTest
public class PostContentRepositoryTest {

	@Autowired
    private PostContentRepository postContentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private Post post;
    private PostContent contentEN;
    private PostContent contentPT;

    @BeforeEach
    void setup() {
        // Criar autor
        author = new User();
        author.setUsername("testAuthor");
        author.setEmail("author@test.com");
        author = userRepository.save(author);

        // Criar post
        post = new Post();
        post.setImageUrl("https://example.com/image.jpg");
        post.setAuthor(author);
        post = postRepository.save(post);

        // Criar conteúdo em inglês
        contentEN = new PostContent();
        contentEN.setLanguage(Language.EN);
        contentEN.setTitle("English Title");
        contentEN.setUrlHandle("english-title");
        contentEN.setContent("English content");
        contentEN.setMetaDescription("English meta description");
        contentEN.setIsDraft(false);
        contentEN.setPost(post);

        // Criar conteúdo em português
        contentPT = new PostContent();
        contentPT.setLanguage(Language.PT);
        contentPT.setTitle("Título em Português");
        contentPT.setUrlHandle("titulo-em-portugues");
        contentPT.setContent("Conteúdo em português");
        contentPT.setMetaDescription("Descrição meta em português");
        contentPT.setIsDraft(false);
        contentPT.setPost(post);

        // Salvar os conteúdos
        contentEN = postContentRepository.save(contentEN);
        contentPT = postContentRepository.save(contentPT);
    }

    @Test
    void shouldFindByUrlHandleAndLanguage() {
        // Test English content
        Optional<PostContent> foundEN = postContentRepository
            .findByUrlHandleAndLanguage("english-title", Language.EN);
        
        assertThat(foundEN).isPresent();
        assertThat(foundEN.get().getTitle()).isEqualTo("English Title");
        assertThat(foundEN.get().getLanguage()).isEqualTo(Language.EN);

        // Test Portuguese content
        Optional<PostContent> foundPT = postContentRepository
            .findByUrlHandleAndLanguage("titulo-em-portugues", Language.PT);
        
        assertThat(foundPT).isPresent();
        assertThat(foundPT.get().getTitle()).isEqualTo("Título em Português");
        assertThat(foundPT.get().getLanguage()).isEqualTo(Language.PT);
    }

    @Test
    void shouldNotFindByInvalidUrlHandle() {
        Optional<PostContent> notFound = postContentRepository
            .findByUrlHandleAndLanguage("non-existent", Language.EN);
        
        assertThat(notFound).isEmpty();
    }

    @Test
    void shouldFindByPostAndLanguage() {
        // Test English content
        Optional<PostContent> foundEN = postContentRepository
            .findByPostAndLanguage(post, Language.EN);
        
        assertThat(foundEN).isPresent();
        assertThat(foundEN.get().getTitle()).isEqualTo("English Title");
        assertThat(foundEN.get().getLanguage()).isEqualTo(Language.EN);

        // Test Portuguese content
        Optional<PostContent> foundPT = postContentRepository
            .findByPostAndLanguage(post, Language.PT);
        
        assertThat(foundPT).isPresent();
        assertThat(foundPT.get().getTitle()).isEqualTo("Título em Português");
        assertThat(foundPT.get().getLanguage()).isEqualTo(Language.PT);
    }

    @Test
    void shouldNotFindByInvalidPostAndLanguage() {
        Post anotherPost = new Post();
        anotherPost.setAuthor(author);
        anotherPost = postRepository.save(anotherPost);

        Optional<PostContent> notFound = postContentRepository
            .findByPostAndLanguage(anotherPost, Language.EN);
        
        assertThat(notFound).isEmpty();
    }
}
