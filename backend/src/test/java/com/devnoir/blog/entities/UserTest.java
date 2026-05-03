package com.devnoir.blog.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

public class UserTest {

	@Test
	void shouldCreateUserWithBasicInfo() {
		User user = new User();
		user.setId(1L);
		user.setUsername("admin");
		user.setFirstName("Admin");
		user.setLastName("User");
		user.setEmail("admin@test.com");
		user.setPassword("123456");
		user.setBio("Bio");
		user.setImageUrl("image.jpg");

		Role role = new Role(1L, "ROLE_ADMIN");
		user.addRole(role);

		assertThat(user.getId()).isEqualTo(1L);
		assertThat(user.getUsername()).isEqualTo("admin");
		assertThat(user.getFirstName()).isEqualTo("Admin");
		assertThat(user.getLastName()).isEqualTo("User");
		assertThat(user.getEmail()).isEqualTo("admin@test.com");
		assertThat(user.getPassword()).isEqualTo("123456");
		assertThat(user.getBio()).isEqualTo("Bio");
		assertThat(user.getImageUrl()).isEqualTo("image.jpg");
		assertThat(user.getRoles()).contains(role);
		assertThat(user.hasRole("ROLE_ADMIN")).isTrue();
		assertThat(user.hasRole("ROLE_WRITER")).isFalse();
	}

	@Test
	void shouldReturnAuthoritiesFromRoles() {
		User user = new User();
		Role role = new Role(1L, "ROLE_ADMIN");
		user.addRole(role);

		assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN");
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
