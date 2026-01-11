package com.devnoir.electricdreams.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.enums.Language;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("SELECT t FROM Tag t WHERE t.language = :language")
    Page<Tag> findAllByLanguage(@Param("language") Language language, Pageable pageable);
	
	List<Tag> findByLanguage(Language language);
	
    Optional<Tag> findByNameAndLanguage(String name, Language language);
    
    boolean existsByNameAndLanguage(String name, Language language);
}
