package com.devnoir.electricdreams.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.enums.Language;

@DataJpaTest
public class TagRepositoryTest {

	@Autowired
    private TagRepository tagRepository;

    @Test
    void shouldSaveAndFindTagById() {
        Tag tag = new Tag();
        tag.setName("kotlin");
        tag.setLanguage(Language.EN);

        Tag saved = tagRepository.save(tag);
        Optional<Tag> found = tagRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("kotlin");
    }
}
