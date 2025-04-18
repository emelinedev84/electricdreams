package com.devnoir.electricdreams.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.devnoir.electricdreams.dto.CategoryDTO;
import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.CategoryRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;

@ExtendWith(MockitoExtension.class)
public class BlogCategoryServiceTest {
	
	@InjectMocks
    private BlogCategoryService blogCategoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindAllCategoriesByLanguage() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setLanguage(Language.PT);

        when(categoryRepository.findByLanguage(any(), any()))
            .thenReturn(new PageImpl<>(List.of(category)));

        Page<CategoryDTO> result = blogCategoryService.findAllByLanguage("PT", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Category 1", result.getContent().get(0).getName());
        assertEquals("PT", result.getContent().get(0).getLanguage());
    }
    
    @Test
    void shouldThrowBusinessExceptionWhenInvalidLanguage() {
    	// First validate the language is invalid
        String invalidLanguage = "invalid";
        
        // Then test the service call
        assertThatThrownBy(() -> {
            blogCategoryService.findAllByLanguage(invalidLanguage, PageRequest.of(0, 10));
        })
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("Idioma inválido: invalid");
    }
}
