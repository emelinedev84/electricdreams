package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devnoir.blog.enums.Language;

public class CategoryTest {

	@Test
    void shouldCreateCategoryWithAllFields() {
        Category category = new Category();
        category.setId(1L);
        category.setCode("books");
        category.setNameEn("Books");
        category.setNamePt("Livros");
        
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getCode()).isEqualTo("books");
        assertThat(category.getNameEn()).isEqualTo("Books");
        assertThat(category.getNamePt()).isEqualTo("Livros");
    }
	
	 @Test
    void shouldReturnLocalizedNameEN() {
        Category category = new Category(1L, "books", "Books", "Livros");

        assertThat(category.getLocalizedName(Language.EN)).isEqualTo("Books");
    }

    @Test
    void shouldReturnLocalizedNamePT() {
        Category category = new Category(1L, "books", "Books", "Livros");

        assertThat(category.getLocalizedName(Language.PT)).isEqualTo("Livros");
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
