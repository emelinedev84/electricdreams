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

import com.devnoir.blog.dto.TagDTO;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.repositories.TagRepository;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminTagServiceTest {

	@Mock
    private TagRepository tagRepository;

    @InjectMocks
    private AdminTagService adminTagService;

    @Test
    void shouldFindAllPaged() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        when(tagRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(java.util.List.of(tag)));

        var result = adminTagService.findAllPaged(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("fiction");
    }

    @Test
    void shouldFindById() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        TagDTO result = adminTagService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("fiction");
    }

    @Test
    void shouldThrowNotFoundWhenTagDoesNotExist() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminTagService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Id not found");
    }

    @Test
    void shouldCreateTag() {
        TagDTO dto = new TagDTO(null, "fiction", "Fiction", "Ficcao");

        when(tagRepository.save(org.mockito.ArgumentMatchers.any(Tag.class)))
                .thenAnswer(invocation -> {
                    Tag tag = invocation.getArgument(0);
                    tag.setId(1L);
                    return tag;
                });

        TagDTO result = adminTagService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("fiction");
    }

    @Test
    void shouldGenerateTagCodeFromNameEnWhenCodeIsBlank() {
        TagDTO dto = new TagDTO(null, "", "Artificial Intelligence", "Inteligencia Artificial");

        when(tagRepository.save(org.mockito.ArgumentMatchers.any(Tag.class)))
                .thenAnswer(invocation -> {
                    Tag tag = invocation.getArgument(0);
                    tag.setId(1L);
                    return tag;
                });

        TagDTO result = adminTagService.create(dto);

        assertThat(result.getCode()).isEqualTo("artificial-intelligence");
    }

    @Test
    void shouldUpdateTag() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");
        TagDTO dto = new TagDTO(1L, "fiction-updated", "Fiction Updated", "Ficcao Atualizada");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);

        TagDTO result = adminTagService.update(1L, dto);

        assertThat(result.getCode()).isEqualTo("fiction-updated");
        assertThat(result.getNameEn()).isEqualTo("Fiction Updated");
    }

    @Test
    void shouldDeleteTagWhenNotInUse() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        adminTagService.delete(1L);

        verify(tagRepository).delete(tag);
    }

    @Test
    void shouldDeleteTagInUseRemovingAssociationFromPosts() {
        Tag tag = new Tag(1L, "fiction", "Fiction", "Ficcao");
        Post post = new Post();
        post.setId(1L);
        post.getTags().add(tag);
        tag.getPosts().add(post);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        adminTagService.delete(1L);

        assertThat(post.getTags()).doesNotContain(tag);
        verify(tagRepository).delete(tag);
    }
}
