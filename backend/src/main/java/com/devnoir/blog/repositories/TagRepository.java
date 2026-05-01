package com.devnoir.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.blog.entities.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByCode(String code);
	
	boolean existsByCode(String code);
}
