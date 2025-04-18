package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.electricdreams.enums.Language;

public class PostContentTest {

	@Test
    void shouldCreatePostContentWithLanguageAndData() {
        PostContent content = new PostContent();
        content.setId(1L);
        content.setTitle("Título de Teste");
        content.setContent("Conteúdo do post");
        content.setLanguage(Language.PT);
        content.setIsDraft(false);

        assertThat(content.getId()).isEqualTo(1L);
        assertThat(content.getTitle()).isEqualTo("Título de Teste");
        assertThat(content.getLanguage()).isEqualTo(Language.PT);
        assertThat(content.getIsDraft()).isFalse();
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        PostContent pc1 = new PostContent();
        pc1.setId(1L);

        PostContent pc2 = new PostContent();
        pc2.setId(1L);

        assertThat(pc1).isEqualTo(pc2);
        assertThat(pc1.hashCode()).isEqualTo(pc2.hashCode());
    }
}