package com.devnoir.electricdreams.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UserTest {

	@Test
    void shouldCreateUserWithBasicInfo() {
        User user = new User();
        user.setId(1L);
        user.setUsername("writer01");
        user.setEmail("writer@example.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("writer01");
        assertThat(user.getEmail()).isEqualTo("writer@example.com");
    }

    @Test
    void shouldMatchEqualsAndHashCode() {
        User u1 = new User();
        u1.setId(1L);

        User u2 = new User();
        u2.setId(1L);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }
}
