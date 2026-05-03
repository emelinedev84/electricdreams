package com.devnoir.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devnoir.blog.dto.CategoryPublicDTO;
import com.devnoir.blog.entities.Category;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class BlogCategoryServiceTest {
	
	@Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogCategoryService blogCategoryService;

    @Test
    void shouldListCategoriesInEN() {
        Category category = new Category(1L, "books", "Books", "Livros");

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryPublicDTO> result = blogCategoryService.findAll(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("books");
        assertThat(result.get(0).getName()).isEqualTo("Books");
    }

    @Test
    void shouldListCategoriesInPT() {
        Category category = new Category(1L, "books", "Books", "Livros");

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryPublicDTO> result = blogCategoryService.findAll(Language.PT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("books");
        assertThat(result.get(0).getName()).isEqualTo("Livros");
    }
}
