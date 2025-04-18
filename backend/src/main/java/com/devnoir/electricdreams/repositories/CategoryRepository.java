package com.devnoir.electricdreams.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("SELECT c FROM Category c WHERE c.language = :language")
    Page<Category> findByLanguage(@Param("language") Language language, Pageable pageable);

	Optional<Category> findByNameAndLanguage(String name, Language valueOf);
	
	boolean existsByNameAndLanguageAndIdNot(String name, Language language, Long id);
}
