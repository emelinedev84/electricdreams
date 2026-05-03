package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.blog.entities.Role;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.import_files="})
public class RoleRepositoryTest {

	@Autowired
    private RoleRepository roleRepository;

	@Test
    void shouldSaveRole() {
        Role role = new Role(null, "ROLE_ADMIN");

        Role saved = roleRepository.save(role);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldFindRoleById() {
        Role saved = roleRepository.save(new Role(null, "ROLE_ADMIN"));

        Optional<Role> result = roleRepository.findById(saved.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindRoleByAuthority() {
        roleRepository.save(new Role(null, "ROLE_ADMIN"));

        Optional<Role> result = roleRepository.findByAuthority("ROLE_ADMIN");

        assertThat(result).isPresent();
        assertThat(result.get().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnEmptyWhenAuthorityDoesNotExist() {
        Optional<Role> result = roleRepository.findByAuthority("ROLE_MISSING");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteRole() {
        Role saved = roleRepository.save(new Role(null, "ROLE_ADMIN"));

        roleRepository.deleteById(saved.getId());

        assertThat(roleRepository.findById(saved.getId())).isEmpty();
    }
}
