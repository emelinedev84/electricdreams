package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.electricdreams.enums.Language;

public class TagTest {

	@Test
    void shouldCreateTagWithNameAndLanguage() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("java");
        tag.setLanguage(Language.EN);

        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getName()).isEqualTo("java");
        assertThat(tag.getLanguage()).isEqualTo(Language.EN);
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        Tag t1 = new Tag();
        t1.setId(1L);

        Tag t2 = new Tag();
        t2.setId(1L);

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }
}
