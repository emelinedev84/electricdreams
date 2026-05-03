package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.devnoir.blog.entities.Category;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.import_files="})
public class PostRepositoryTest {

	@Autowired
    private PostRepository postRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	private User author;
    private Category books;
    private Category games;
    private Tag fiction;
    private Tag technology;
    private Post postWithEnAndPt;
    private Post postOnlyEn;
    private Post postWithPtDraft;

    @BeforeEach
    void setup() {
        author = new User();
        author.setUsername("admin");
        author.setEmail("admin@test.com");
        author.setPassword("123456");
        author = userRepository.save(author);

        books = categoryRepository.save(new Category(null, "books", "Books", "Livros"));
        games = categoryRepository.save(new Category(null, "games", "Games", "Jogos"));
        fiction = tagRepository.save(new Tag(null, "fiction", "Fiction", "Ficcao"));
        technology = tagRepository.save(new Tag(null, "technology", "Technology", "Tecnologia"));

        postWithEnAndPt = savePost("p1.jpg", books, fiction,
                content(Language.EN, "blade-runner", "Blade Runner", PostContentStatus.PUBLISHED),
                content(Language.PT, "blade-runner-pt", "Blade Runner PT", PostContentStatus.PUBLISHED));

        postOnlyEn = savePost("p2.jpg", books, technology,
                content(Language.EN, "future-of-ai", "Future of AI", PostContentStatus.PUBLISHED));

        postWithPtDraft = savePost("p3.jpg", games, fiction,
                content(Language.EN, "digital-utopia", "Digital Utopia", PostContentStatus.PUBLISHED),
                content(Language.PT, "utopia-digital", "Utopia Digital", PostContentStatus.DRAFT));
    }

    @Test
    void shouldSavePost() {
        Post post = new Post();
        post.setAuthor(author);
        post.setImageUrl("image.jpg");

        Post saved = postRepository.save(post);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldFindPostById() {
        Optional<Post> result = postRepository.findById(postWithEnAndPt.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindByIdWithContentsTagsAndCategories() {
        Optional<Post> result = postRepository.findByIdWithContentsTagsAndCategories(postWithEnAndPt.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getContents()).hasSize(2);
        assertThat(result.get().getTags()).isNotEmpty();
        assertThat(result.get().getCategories()).isNotEmpty();
    }

    @Test
    void shouldFindPublicPostsByLanguageEN() {
        Page<Post> page = postRepository.findPublicPostsByLanguage(Language.EN, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void shouldFindPublicPostsByLanguagePTWithoutFallback() {
        Page<Post> page = postRepository.findPublicPostsByLanguage(Language.PT, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent()).extracting(Post::getId).contains(postWithEnAndPt.getId());
        assertThat(page.getContent()).extracting(Post::getId).doesNotContain(postOnlyEn.getId());
        assertThat(page.getContent()).extracting(Post::getId).doesNotContain(postWithPtDraft.getId());
    }

    @Test
    void shouldFindPublicPostByUrlHandleAndLanguage() {
        Optional<Post> result = postRepository.findPublicPostByUrlHandleAndLanguage("future-of-ai", Language.EN);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(postOnlyEn.getId());
    }

    @Test
    void shouldNotFindDraftPostByUrlHandleAndLanguage() {
        Optional<Post> result = postRepository.findPublicPostByUrlHandleAndLanguage("utopia-digital", Language.PT);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindPublicPostsByCategoryCodeAndLanguageEN() {
        Page<Post> page = postRepository.findPublicPostsByCategoryCodeAndLanguage(
                "books",
                Language.EN,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void shouldFindPublicPostsByCategoryCodeAndLanguagePTWithoutFallback() {
        Page<Post> page = postRepository.findPublicPostsByCategoryCodeAndLanguage(
                "books",
                Language.PT,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(postWithEnAndPt.getId());
    }

    @Test
    void shouldFindPublicPostsByTagCodeAndLanguageEN() {
        Page<Post> page = postRepository.findPublicPostsByTagCodeAndLanguage(
                "fiction",
                Language.EN,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void shouldFindPublicPostsByTagCodeAndLanguagePTWithoutFallback() {
        Page<Post> page = postRepository.findPublicPostsByTagCodeAndLanguage(
                "fiction",
                Language.PT,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void shouldFindByContentsUrlHandleAndContentsLanguage() {
        Optional<Post> result = postRepository.findByContentsUrlHandleAndContentsLanguage("future-of-ai", Language.EN);

        assertThat(result).isPresent();
    }

    @Test
    void shouldDeletePost() {
        Long id = postOnlyEn.getId();

        postRepository.deleteById(id);

        assertThat(postRepository.findById(id)).isEmpty();
    }

    private Post savePost(String imageUrl, Category category, Tag tag, PostContent... contents) {
        Post post = new Post();
        post.setAuthor(author);
        post.setImageUrl(imageUrl);
        post.getCategories().add(category);
        post.getTags().add(tag);

        for (PostContent content : contents) {
            content.setPost(post);
            post.getContents().add(content);
        }

        return postRepository.save(post);
    }

    private PostContent content(Language language, String urlHandle, String title, PostContentStatus status) {
        PostContent content = new PostContent();
        content.setLanguage(language);
        content.setUrlHandle(urlHandle);
        content.setTitle(title);
        content.setContent("Content");
        content.setMetaDescription("Meta description");
        content.setStatus(status);
        return content;
    }
}
