package com.devnoir.electricdreams.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;

@DataJpaTest
public class CategoryRepositoryTest {

	@Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveAndFindCategoryById() {
        Category category = new Category();
        category.setName("Ciência");
        category.setLanguage(Language.PT);

        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ciência");
    }

    @Test
    void shouldDeleteCategory() {
        Category category = new Category();
        category.setName("Music");
        category.setLanguage(Language.EN);

        Category saved = categoryRepository.save(category);
        categoryRepository.delete(saved);

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
