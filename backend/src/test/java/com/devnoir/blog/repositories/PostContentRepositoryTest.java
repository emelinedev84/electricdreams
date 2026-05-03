package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.import_files="})
public class PostContentRepositoryTest {

	@Autowired
    private PostContentRepository postContentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post;

    @BeforeEach
    void setup() {
        User author = new User();
        author.setUsername("admin");
        author.setEmail("admin@test.com");
        author.setPassword("123456");
        author = userRepository.save(author);

        post = new Post();
        post.setAuthor(author);
        post = postRepository.save(post);
    }

    @Test
    void shouldSavePostContent() {
        PostContent content = content("test-post", Language.EN);

        PostContent saved = postContentRepository.save(content);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrlHandle()).isEqualTo("test-post");
    }

    @Test
    void shouldFindByUrlHandleAndLanguage() {
        postContentRepository.save(content("test-post", Language.EN));

        Optional<PostContent> result = postContentRepository.findByUrlHandleAndLanguage("test-post", Language.EN);

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindByPostAndLanguage() {
        postContentRepository.save(content("test-post", Language.EN));

        Optional<PostContent> result = postContentRepository.findByPostAndLanguage(post, Language.EN);

        assertThat(result).isPresent();
    }

    @Test
    void shouldCheckIfUrlHandleAndLanguageExists() {
        postContentRepository.save(content("test-post", Language.EN));

        boolean exists = postContentRepository.existsByUrlHandleAndLanguage("test-post", Language.EN);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfPostIdAndLanguageExists() {
        postContentRepository.save(content("test-post", Language.EN));

        boolean exists = postContentRepository.existsByPostIdAndLanguage(post.getId(), Language.EN);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfUrlHandleAndLanguageExistsIgnoringCurrentId() {
        PostContent saved = postContentRepository.save(content("test-post", Language.EN));

        boolean exists = postContentRepository.existsByUrlHandleAndLanguageAndIdNot(
                "test-post",
                Language.EN,
                saved.getId()
        );

        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeletePostContent() {
        PostContent saved = postContentRepository.save(content("test-post", Language.EN));

        postContentRepository.deleteById(saved.getId());

        assertThat(postContentRepository.findById(saved.getId())).isEmpty();
    }

    private PostContent content(String urlHandle, Language language) {
        PostContent content = new PostContent();
        content.setPost(post);
        content.setLanguage(language);
        content.setUrlHandle(urlHandle);
        content.setTitle("Title");
        content.setContent("Content");
        content.setMetaDescription("Meta description");
        content.setStatus(PostContentStatus.PUBLISHED);
        return content;
    }
}
