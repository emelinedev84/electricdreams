package com.devnoir.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.PostContentRepository;

@ExtendWith(MockitoExtension.class)
public class UrlHandleServiceTest {

	@Mock
    private PostContentRepository contentRepository;

    @InjectMocks
    private UrlHandleService urlHandleService;

    @Test
    void shouldGenerateSlugFromTitle() {
        when(contentRepository.existsByUrlHandleAndLanguage("ola-mundo", Language.PT)).thenReturn(false);

        String result = urlHandleService.generateUniqueHandle("Olá Mundo!", Language.PT);

        assertThat(result).isEqualTo("ola-mundo");
    }

    @Test
    void shouldUsePostWhenTitleDoesNotGenerateSlug() {
        when(contentRepository.existsByUrlHandleAndLanguage("post", Language.EN)).thenReturn(false);

        String result = urlHandleService.generateUniqueHandle("!!!", Language.EN);

        assertThat(result).isEqualTo("post");
    }

    @Test
    void shouldAddSuffixWhenSlugAlreadyExists() {
        when(contentRepository.existsByUrlHandleAndLanguage("future-of-ai", Language.EN)).thenReturn(true);
        when(contentRepository.existsByUrlHandleAndLanguage("future-of-ai-2", Language.EN)).thenReturn(false);

        String result = urlHandleService.generateUniqueHandle("Future of AI", Language.EN);

        assertThat(result).isEqualTo("future-of-ai-2");
    }

    @Test
    void shouldGenerateUniqueHandleForUpdateIgnoringCurrentContent() {
        when(contentRepository.existsByUrlHandleAndLanguageAndIdNot("future-of-ai", Language.EN, 1L))
                .thenReturn(false);

        String result = urlHandleService.generateUniqueHandleForUpdate("Future of AI", Language.EN, 1L);

        assertThat(result).isEqualTo("future-of-ai");
    }

    @Test
    void shouldAddSuffixForUpdateWhenAnotherContentUsesHandle() {
        when(contentRepository.existsByUrlHandleAndLanguageAndIdNot("future-of-ai", Language.EN, 1L))
                .thenReturn(true);
        when(contentRepository.existsByUrlHandleAndLanguageAndIdNot("future-of-ai-2", Language.EN, 1L))
                .thenReturn(false);

        String result = urlHandleService.generateUniqueHandleForUpdate("Future of AI", Language.EN, 1L);

        assertThat(result).isEqualTo("future-of-ai-2");
    }
}
