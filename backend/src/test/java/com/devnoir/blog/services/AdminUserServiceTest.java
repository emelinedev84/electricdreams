package com.devnoir.blog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devnoir.blog.dto.RoleDTO;
import com.devnoir.blog.dto.UserCreateDTO;
import com.devnoir.blog.dto.UserDTO;
import com.devnoir.blog.dto.UserProfileDTO;
import com.devnoir.blog.dto.UserRoleDTO;
import com.devnoir.blog.dto.UserUpdateDTO;
import com.devnoir.blog.entities.Role;
import com.devnoir.blog.entities.User;
import com.devnoir.blog.projections.UserDetailsProjection;
import com.devnoir.blog.repositories.RoleRepository;
import com.devnoir.blog.repositories.UserRepository;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {

	@InjectMocks
    private AdminUserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private Role adminRole;
    private Role otherRole;
    private User user;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1L, "ROLE_ADMIN");
        otherRole = new Role(2L, "ROLE_OTHER");

        user = new User();
        user.setId(10L);
        user.setUsername("admin");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail("admin@email.com");
        user.setBio("Admin bio");
        user.setImageUrl("https://img.com/admin.jpg");
        user.setPassword("encoded-password");
        user.getRoles().add(adminRole);
    }

    @Test
    void findAllPagedShouldReturnPagedUserDTO() {
        when(userRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(user)));

        var result = service.findAllPaged(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("admin", result.getContent().get(0).getUsername());
    }

    @Test
    void findByIdShouldReturnUserDTOWhenUserExists() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        UserDTO result = service.findById(10L);

        assertEquals(10L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("admin@email.com", result.getEmail());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void findByUsernameShouldReturnUserDTOWhenUserExists() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDTO result = service.findByUsername("admin");

        assertEquals(10L, result.getId());
        assertEquals("admin", result.getUsername());
    }

    @Test
    void findByUsernameShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByUsername("missing"));
    }

    @Test
    void createShouldEncodePasswordAndUseProvidedRole() {
        UserCreateDTO dto = validCreateDTO();
        dto.getRoles().add(new RoleDTO(1L, "ROLE_ADMIN"));

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("Admin@123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UserDTO result = service.create(dto);

        assertEquals(10L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("admin@email.com", result.getEmail());
        assertEquals(1, result.getRoles().size());

        verify(passwordEncoder).encode("Admin@123");
    }

    @Test
    void createShouldAddDefaultAdminRoleWhenDtoHasNoRoles() {
        UserCreateDTO dto = validCreateDTO();

        when(passwordEncoder.encode("Admin@123")).thenReturn("encoded-password");
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UserDTO result = service.create(dto);

        assertEquals(10L, result.getId());
        assertEquals(1, result.getRoles().size());
        assertEquals("ROLE_ADMIN", result.getRoles().iterator().next().getAuthority());

        verify(roleRepository).findByAuthority("ROLE_ADMIN");
    }

    @Test
    void createShouldThrowResourceNotFoundExceptionWhenProvidedRoleDoesNotExist() {
        UserCreateDTO dto = validCreateDTO();
        dto.getRoles().add(new RoleDTO(999L, "ROLE_ADMIN"));

        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
    }

    @Test
    void updateShouldCopyEditableFieldsAndRoles() {
        UserUpdateDTO dto = validUpdateDTO();
        dto.getRoles().add(new RoleDTO(2L, "ROLE_OTHER"));

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(otherRole));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = service.update(10L, dto);

        assertEquals("updated-admin", result.getUsername());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("ROLE_OTHER", result.getRoles().iterator().next().getAuthority());
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        UserUpdateDTO dto = validUpdateDTO();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, dto));
    }

    @Test
    void updateRoleShouldReplaceCurrentRole() {
        UserRoleDTO dto = new UserRoleDTO("ROLE_OTHER");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findByAuthority("ROLE_OTHER")).thenReturn(Optional.of(otherRole));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = service.updateRole(10L, dto);

        assertEquals(1, result.getRoles().size());
        assertEquals("ROLE_OTHER", result.getRoles().iterator().next().getAuthority());
    }

    @Test
    void updateRoleShouldThrowResourceNotFoundExceptionWhenRoleDoesNotExist() {
        UserRoleDTO dto = new UserRoleDTO("ROLE_MISSING");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findByAuthority("ROLE_MISSING")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateRole(10L, dto));
    }

    @Test
    void updateProfileShouldUpdateProfileFieldsAndEmail() {
        UserProfileDTO dto = new UserProfileDTO(
                "New",
                "Name",
                "New bio",
                "https://img.com/new.jpg",
                "new@email.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = service.updateProfile(10L, dto);

        assertEquals("New", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("New bio", result.getBio());
        assertEquals("https://img.com/new.jpg", result.getImageUrl());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateProfileShouldAllowKeepingSameEmail() {
        UserProfileDTO dto = new UserProfileDTO(
                "Admin",
                "User",
                "Same email bio",
                "https://img.com/admin.jpg",
                "admin@email.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = service.updateProfile(10L, dto);

        assertEquals("admin@email.com", result.getEmail());
        assertEquals("Same email bio", result.getBio());
    }

    @Test
    void updateProfileShouldThrowBusinessExceptionWhenEmailBelongsToAnotherUser() {
        User otherUser = new User();
        otherUser.setId(20L);
        otherUser.setEmail("used@email.com");

        UserProfileDTO dto = new UserProfileDTO(
                "Admin",
                "User",
                "Bio",
                "https://img.com/admin.jpg",
                "used@email.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("used@email.com")).thenReturn(Optional.of(otherUser));

        assertThrows(BusinessException.class, () -> service.updateProfile(10L, dto));
    }

    @Test
    void deleteShouldDeleteUserWhenUserExists() {
        when(userRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(userRepository).deleteById(10L);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
    }

    @Test
    void loadUserByUsernameShouldReturnSpringSecurityUserDetails() {
        when(userRepository.searchUserAndRolesByUsername("admin"))
                .thenReturn(List.of(projection("admin", "encoded-password", 1L, "ROLE_ADMIN")));

        var result = service.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist() {
        when(userRepository.searchUserAndRolesByUsername("missing"))
                .thenReturn(List.of());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }

    private UserCreateDTO validCreateDTO() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("admin");
        dto.setFirstName("Admin");
        dto.setLastName("User");
        dto.setEmail("admin@email.com");
        dto.setBio("Admin bio");
        dto.setImageUrl("https://img.com/admin.jpg");
        dto.setPassword("Admin@123");
        return dto;
    }

    private UserUpdateDTO validUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("updated-admin");
        dto.setFirstName("Updated");
        dto.setLastName("Admin");
        dto.setEmail("updated@email.com");
        dto.setBio("Updated bio");
        dto.setImageUrl("https://img.com/updated.jpg");
        return dto;
    }

    private UserDetailsProjection projection(
            String username,
            String password,
            Long roleId,
            String authority) {
        return new UserDetailsProjection() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public Long getRoleId() {
                return roleId;
            }

            @Override
            public String getAuthority() {
                return authority;
            }
        };
    }
}
