package com.devnoir.electricdreams.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
