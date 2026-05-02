package com.devnoir.blog.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.enums.Language;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT DISTINCT p FROM Post p " +
	       "LEFT JOIN FETCH p.contents c " +
	       "LEFT JOIN FETCH p.tags " +
	       "LEFT JOIN FETCH p.categories " +
	       "WHERE p.id = :id")
	Optional<Post> findByIdWithContentsTagsAndCategories(@Param("id") Long id);

	@Query("SELECT DISTINCT p FROM Post p " +
	       "LEFT JOIN FETCH p.contents c " +
	       "LEFT JOIN FETCH p.tags " +
	       "LEFT JOIN FETCH p.categories " +
	       "WHERE c.language = :language AND c.status = 'PUBLISHED'")
	Page<Post> findByContentsLanguageAndContentsStatusPublishedWithTagsAndCategories(@Param("language") Language language, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Post p " +
		   "LEFT JOIN FETCH p.contents " +
		   "LEFT JOIN FETCH p.tags " +
		   "LEFT JOIN FETCH p.categories " +
		   "JOIN p.contents c " +
		   "WHERE c.language = 'EN' AND c.status = 'PUBLISHED'")
	Page<Post> findPublicPostsWithPublishedEn(Pageable pageable);
	
	@Query("""
	       SELECT DISTINCT p FROM Post p
	       LEFT JOIN FETCH p.contents
	       LEFT JOIN FETCH p.tags
	       LEFT JOIN FETCH p.categories categories
	       JOIN p.contents c
	       WHERE categories.code = :categoryCode
	       AND c.language = 'EN'
	       AND c.status = 'PUBLISHED'
	       """)
	Page<Post> findPublicPostsByCategoryCode(@Param("categoryCode") String categoryCode, Pageable pageable);
	
	@Query("""
	       SELECT DISTINCT p FROM Post p
	       LEFT JOIN FETCH p.contents
	       LEFT JOIN FETCH p.tags tags
	       LEFT JOIN FETCH p.categories
	       JOIN p.contents c
	       WHERE tags.code = :tagCode
	       AND c.language = 'EN'
	       AND c.status = 'PUBLISHED'
	       """)
	Page<Post> findPublicPostsByTagCode(@Param("tagCode") String tagCode, Pageable pageable);
	
	@Query("SELECT DISTINCT p FROM Post p " +
		   "LEFT JOIN FETCH p.contents " +
		   "LEFT JOIN FETCH p.tags " +
		   "LEFT JOIN FETCH p.categories " +
		   "JOIN p.contents c " +
		   "WHERE c.urlHandle = :urlHandle " +
		   "AND c.language = :language " +
		   "AND c.status = 'PUBLISHED'")
	Optional<Post> findPublicPostByUrlHandleAndLanguage(@Param("urlHandle") String urlHandle, @Param("language") Language language);
	
	@Query("SELECT DISTINCT p FROM Post p " +
	       "LEFT JOIN FETCH p.contents c " +
	       "LEFT JOIN FETCH p.tags " +
	       "LEFT JOIN FETCH p.categories " +
	       "WHERE c.urlHandle = :urlHandle AND c.language = :language AND c.status = 'PUBLISHED'")
	Optional<Post> findByContentsUrlHandleAndContentsLanguageAndContentsStatusPublishedWithTagsAndCategories(
			@Param("urlHandle") String urlHandle,
			@Param("language") Language language);

	@Query("SELECT DISTINCT p FROM Post p JOIN p.contents c WHERE c.language = :language AND c.status = 'PUBLISHED'")
	Page<Post> findByContentsLanguageAndContentsStatusPublished(@Param("language") Language language, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Post p " +
	       "LEFT JOIN FETCH p.contents c " +
	       "LEFT JOIN FETCH p.tags " +
	       "LEFT JOIN FETCH p.categories " +
	       "WHERE p.id = :id")
	Optional<Post> findByIdWithContents(@Param("id") Long id);

	Optional<Post> findByContentsUrlHandleAndContentsLanguage(String urlHandle, Language language);

	Optional<Post> findByContentsLanguage(Language language);
}