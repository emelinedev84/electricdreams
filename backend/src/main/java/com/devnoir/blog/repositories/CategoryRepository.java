package com.devnoir.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.blog.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
		
	Optional<Category> findByCode(String code);
	
	boolean existsByCode(String code);
}
