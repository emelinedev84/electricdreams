package com.devnoir.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.devnoir.blog.dto.CategoryDTO;
import com.devnoir.blog.entities.Category;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.repositories.CategoryRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryServiceTest {

	@Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminCategoryService adminCategoryService;

    @Test
    void shouldFindAllPaged() {
        Category category = new Category(1L, "books", "Books", "Livros");

        when(categoryRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(java.util.List.of(category)));

        var result = adminCategoryService.findAllPaged(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("books");
    }

    @Test
    void shouldFindById() {
        Category category = new Category(1L, "books", "Books", "Livros");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = adminCategoryService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("books");
    }

    @Test
    void shouldThrowNotFoundWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCategoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Id not found");
    }

    @Test
    void shouldCreateCategory() {
        CategoryDTO dto = new CategoryDTO(null, "books", "Books", "Livros");

        when(categoryRepository.existsByCode("books")).thenReturn(false);
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(1L);
                    return category;
                });

        CategoryDTO result = adminCategoryService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("books");
        assertThat(result.getNameEn()).isEqualTo("Books");
        assertThat(result.getNamePt()).isEqualTo("Livros");
    }

    @Test
    void shouldGenerateCategoryCodeFromNameEnWhenCodeIsBlank() {
        CategoryDTO dto = new CategoryDTO(null, "", "Science Fiction", "Ficcao Cientifica");

        when(categoryRepository.existsByCode("science-fiction")).thenReturn(false);
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(1L);
                    return category;
                });

        CategoryDTO result = adminCategoryService.create(dto);

        assertThat(result.getCode()).isEqualTo("science-fiction");
    }

    @Test
    void shouldNotCreateCategoryWithDuplicateCode() {
        CategoryDTO dto = new CategoryDTO(null, "books", "Books", "Livros");

        when(categoryRepository.existsByCode("books")).thenReturn(true);

        assertThatThrownBy(() -> adminCategoryService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Category code already exists");
    }

    @Test
    void shouldUpdateCategory() {
        Category category = new Category(1L, "books", "Books", "Livros");
        CategoryDTO dto = new CategoryDTO(1L, "books-updated", "Books Updated", "Livros Atualizados");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByCode("books-updated")).thenReturn(Optional.empty());
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDTO result = adminCategoryService.update(1L, dto);

        assertThat(result.getCode()).isEqualTo("books-updated");
        assertThat(result.getNameEn()).isEqualTo("Books Updated");
    }

    @Test
    void shouldNotUpdateCategoryToDuplicateCode() {
        Category category = new Category(1L, "books", "Books", "Livros");
        Category other = new Category(2L, "games", "Games", "Jogos");
        CategoryDTO dto = new CategoryDTO(1L, "games", "Games", "Jogos");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByCode("games")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> adminCategoryService.update(1L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Category code already exists");
    }

    @Test
    void shouldDeleteCategoryWhenNotInUse() {
        Category category = new Category(1L, "books", "Books", "Livros");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        adminCategoryService.delete(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldNotDeleteCategoryWhenInUse() {
        Category category = new Category(1L, "books", "Books", "Livros");
        Post post = new Post();
        post.setId(1L);

        category.getPosts().add(post);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> adminCategoryService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete a category");
    }
}
