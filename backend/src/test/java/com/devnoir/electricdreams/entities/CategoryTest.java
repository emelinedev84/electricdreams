package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.electricdreams.enums.Language;

public class CategoryTest {

	@Test
    void shouldCreateCategoryWithAllFields() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Tecnologia");
        category.setLanguage(Language.PT);

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Tecnologia");
        assertThat(category.getLanguage()).isEqualTo(Language.PT);
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        Category cat1 = new Category();
        cat1.setId(1L);

        Category cat2 = new Category();
        cat2.setId(1L);

        assertThat(cat1).isEqualTo(cat2);
        assertThat(cat1.hashCode()).isEqualTo(cat2.hashCode());
    }
}
