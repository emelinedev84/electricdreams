package com.devnoir.blog.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devnoir.blog.entities.Role;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.projections.UserDetailsProjection;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
    private UserRepository userRepository;

	@Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveUser() {
        User user = user("admin", "admin@test.com");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("admin");
    }

    @Test
    void shouldFindUserById() {
        User saved = userRepository.save(user("admin", "admin@test.com"));

        Optional<User> result = userRepository.findById(saved.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindByUsername() {
        userRepository.save(user("admin", "admin@test.com"));

        Optional<User> result = userRepository.findByUsername("admin");

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindByEmail() {
        userRepository.save(user("admin", "admin@test.com"));

        Optional<User> result = userRepository.findByEmail("admin@test.com");

        assertThat(result).isPresent();
    }

    @Test
    void shouldCheckIfUsernameExists() {
        userRepository.save(user("admin", "admin@test.com"));

        assertThat(userRepository.existsByUsername("admin")).isTrue();
    }

    @Test
    void shouldCheckIfEmailExists() {
        userRepository.save(user("admin", "admin@test.com"));

        assertThat(userRepository.existsByEmail("admin@test.com")).isTrue();
    }

    @Test
    void shouldCheckIfEmailExistsForAnotherUser() {
        User user1 = userRepository.save(user("admin", "admin@test.com"));
        userRepository.save(user("other", "other@test.com"));

        boolean exists = userRepository.existsByEmailAndIdNot("other@test.com", user1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfUsernameExistsForAnotherUser() {
        User user1 = userRepository.save(user("admin", "admin@test.com"));
        userRepository.save(user("other", "other@test.com"));

        boolean exists = userRepository.existsByUsernameAndIdNot("other", user1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldSearchUserAndRolesByUsername() {
        Role role = roleRepository.save(new Role(null, "ROLE_ADMIN"));

        User user = user("admin", "admin@test.com");
        user.setPassword("encoded");
        user.getRoles().add(role);
        userRepository.save(user);

        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByUsername("admin");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("admin");
        assertThat(result.get(0).getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldDeleteUser() {
        User saved = userRepository.save(user("admin", "admin@test.com"));

        userRepository.deleteById(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    private User user(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("123456");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setBio("Bio");
        user.setImageUrl("image.jpg");
        return user;
    }
}
