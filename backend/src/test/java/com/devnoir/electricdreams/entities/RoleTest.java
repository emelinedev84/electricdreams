package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RoleTest {

	@Test
    void shouldCreateRoleWithAllFields() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("WRITER");

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getAuthority()).isEqualTo("WRITER");
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
