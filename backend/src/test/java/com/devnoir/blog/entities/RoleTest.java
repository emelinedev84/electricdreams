package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RoleTest {

	@Test
    void shouldCreateRole() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_ADMIN");

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        Role r1 = new Role();
        r1.setId(1L);

        Role r2 = new Role();
        r2.setId(1L);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }
}
