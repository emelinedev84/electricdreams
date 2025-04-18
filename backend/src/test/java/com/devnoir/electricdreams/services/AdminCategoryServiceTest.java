package com.devnoir.electricdreams.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.services.exceptions.DatabaseException;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryServiceTest {

	@Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminCategoryService service;
    
	// Caso de uso: Buscar categoria por ID
    @Test
    void shouldGetCategoryByIdWhenExists() {
    	Category category = new Category();
        category.setId(1L);
        category.setName("Technology");
        category.setLanguage(Language.EN);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryDTO result = service.findById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Technology");
        assertThat(result.getLanguage()).isEqualTo("EN");
    }

    // Caso de uso: Atualizar nome da categoria
    @Test
    void shouldUpdateCategoryName() {
    	// Arrange
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");
        existingCategory.setLanguage(Language.EN);

        CategoryDTO updateDto = new CategoryDTO();
        updateDto.setId(1L);
        updateDto.setName("New Name");
        updateDto.setLanguage("EN");

        when(categoryRepository.getReferenceById(1L)).thenReturn(existingCategory);
        when(categoryRepository.findByNameAndLanguage("New Name", Language.EN)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CategoryDTO result = service.update(1L, updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Name");
        verify(categoryRepository).save(any(Category.class));
    }

    // Caso de uso: Excluir categoria sem vínculo
    @Test
    void shouldDeleteCategoryWhenNotUsed() {
    	// Arrange
        Category category = new Category();
        category.setId(1L);
        
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        service.delete(1L);
        verify(categoryRepository).deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingUsedCategory() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(1L);

        // Act & Assert
        assertThatThrownBy(() -> service.delete(1L))
            .isInstanceOf(DatabaseException.class);
    }
}
