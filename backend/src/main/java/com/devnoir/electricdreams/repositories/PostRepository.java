package com.devnoir.electricdreams.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.enums.Language;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT DISTINCT p FROM Post p JOIN p.contents c WHERE c.language = :language AND c.isDraft = false")
    Page<Post> findByContentsLanguageAndContentsIsDraftFalse(
        @Param("language") Language language, 
        Pageable pageable);

	Optional<Post> findByContentsUrlHandleAndContentsLanguageAndContentsIsDraftFalse(String urlHandle, Language lang);
	
	@Query("SELECT p FROM Post p LEFT JOIN FETCH p.contents WHERE p.id = :id")
	Optional<Post> findByIdWithContents(@Param("id") Long id);
}