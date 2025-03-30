package com.devnoir.electricdreams.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
