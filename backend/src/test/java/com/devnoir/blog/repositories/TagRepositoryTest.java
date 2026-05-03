package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.blog.entities.Tag;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.import_files="})
public class TagRepositoryTest {

	@Autowired
    private TagRepository tagRepository;

	@Test
    void shouldSaveTag() {
        Tag tag = new Tag(null, "fiction", "Fiction", "Ficcao");

        Tag saved = tagRepository.save(tag);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("fiction");
    }

    @Test
    void shouldFindTagById() {
        Tag saved = tagRepository.save(new Tag(null, "fiction", "Fiction", "Ficcao"));

        Optional<Tag> result = tagRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("fiction");
    }

    @Test
    void shouldFindTagByCode() {
        tagRepository.save(new Tag(null, "fiction", "Fiction", "Ficcao"));

        Optional<Tag> result = tagRepository.findByCode("fiction");

        assertThat(result).isPresent();
        assertThat(result.get().getNamePt()).isEqualTo("Ficcao");
    }

    @Test
    void shouldReturnEmptyWhenCodeDoesNotExist() {
        Optional<Tag> result = tagRepository.findByCode("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckIfCodeExists() {
        tagRepository.save(new Tag(null, "fiction", "Fiction", "Ficcao"));

        boolean exists = tagRepository.existsByCode("fiction");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteTag() {
        Tag saved = tagRepository.save(new Tag(null, "fiction", "Fiction", "Ficcao"));

        tagRepository.deleteById(saved.getId());

        assertThat(tagRepository.findById(saved.getId())).isEmpty();
    }
}
