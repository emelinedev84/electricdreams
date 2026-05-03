package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.blog.entities.Category;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.import_files="})
public class CategoryRepositoryTest {

	@Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveCategory() {
        Category category = new Category(null, "books", "Books", "Livros");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("books");
    }

    @Test
    void shouldFindCategoryById() {
        Category saved = categoryRepository.save(new Category(null, "books", "Books", "Livros"));

        Optional<Category> result = categoryRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("books");
    }

    @Test
    void shouldFindCategoryByCode() {
        categoryRepository.save(new Category(null, "books", "Books", "Livros"));

        Optional<Category> result = categoryRepository.findByCode("books");

        assertThat(result).isPresent();
        assertThat(result.get().getNameEn()).isEqualTo("Books");
    }

    @Test
    void shouldReturnEmptyWhenCodeDoesNotExist() {
        Optional<Category> result = categoryRepository.findByCode("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckIfCodeExists() {
        categoryRepository.save(new Category(null, "books", "Books", "Livros"));

        boolean exists = categoryRepository.existsByCode("books");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteCategory() {
        Category saved = categoryRepository.save(new Category(null, "books", "Books", "Livros"));

        categoryRepository.deleteById(saved.getId());

        assertThat(categoryRepository.findById(saved.getId())).isEmpty();
    }
}
