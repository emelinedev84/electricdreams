package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.blog.enums.Language;

public class TagTest {

	@Test
    void shouldCreateTagWithAllFields() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setCode("technology");
        tag.setNameEn("Technology");
        tag.setNamePt("Tecnologia");

        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getCode()).isEqualTo("technology");
        assertThat(tag.getNameEn()).isEqualTo("Technology");
        assertThat(tag.getNamePt()).isEqualTo("Tecnologia");
    }

	@Test
    void shouldReturnLocalizedNameEN() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        assertThat(tag.getLocalizedName(Language.EN)).isEqualTo("Fiction");
    }

    @Test
    void shouldReturnLocalizedNamePT() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        assertThat(tag.getLocalizedName(Language.PT)).isEqualTo("Ficcao");
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
