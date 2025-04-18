package com.devnoir.electricdreams.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.electricdreams.entities.Role;

@DataJpaTest
public class RoleRepositoryTest {

	@Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveAndFindRoleById() {
        Role role = new Role();
        role.setAuthority("ADMIN");

        Role saved = roleRepository.save(role);
        Optional<Role> found = roleRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAuthority()).isEqualTo("ADMIN");
    }
}
