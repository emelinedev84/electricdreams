package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.blog.enums.Language;
import com.devnoir.blog.enums.PostContentStatus;

public class PostContentTest {

	@Test
    void shouldCreatePostContentWithLanguageAndStatus() {
		Post post = new Post();
		post.setId(1L);
		
        PostContent content = new PostContent();
        content.setId(1L);
        content.setLanguage(Language.EN);
        content.setUrlHandle("test-post");
        content.setTitle("Test Post");
        content.setContent("Test content");
        content.setMetaDescription("Test meta description");
        content.setStatus(PostContentStatus.PUBLISHED);
        content.setPost(post);
        
        assertThat(content.getId()).isEqualTo(1L);
        assertThat(content.getLanguage()).isEqualTo(Language.EN);
        assertThat(content.getUrlHandle()).isEqualTo("test-post");
        assertThat(content.getTitle()).isEqualTo("Test Post");
        assertThat(content.getContent()).isEqualTo("Test content");
        assertThat(content.getMetaDescription()).isEqualTo("Test meta description");
        assertThat(content.getStatus()).isEqualTo(PostContentStatus.PUBLISHED);
        assertThat(content.getPost()).isEqualTo(post);
    }

	@Test
    void shouldComparePostContentsByIdAndLanguage() {
        PostContent c1 = new PostContent();
        c1.setId(1L);
        c1.setLanguage(Language.EN);

        PostContent c2 = new PostContent();
        c2.setId(1L);
        c2.setLanguage(Language.EN);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldNotEqualWhenLanguageIsDifferent() {
        PostContent c1 = new PostContent();
        c1.setId(1L);
        c1.setLanguage(Language.EN);

        PostContent c2 = new PostContent();
        c2.setId(1L);
        c2.setLanguage(Language.PT);

        assertThat(c1).isNotEqualTo(c2);
    }
}