package com.devnoir.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devnoir.blog.dto.TagPublicDTO;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.TagRepository;

@ExtendWith(MockitoExtension.class)
public class BlogTagServiceTest {

	@Mock
    private TagRepository tagRepository;

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogTagService blogTagService;

    @Test
    void shouldListTagsInEN() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        when(tagRepository.findAll()).thenReturn(List.of(tag));

        List<TagPublicDTO> result = blogTagService.findAll(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("fiction");
        assertThat(result.get(0).getName()).isEqualTo("Fiction");
    }

    @Test
    void shouldListTagsInPT() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        when(tagRepository.findAll()).thenReturn(List.of(tag));

        List<TagPublicDTO> result = blogTagService.findAll(Language.PT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("fiction");
        assertThat(result.get(0).getName()).isEqualTo("Ficcao");
    }
}
