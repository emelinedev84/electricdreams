package com.devnoir.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.PostContent;
import com.devnoir.blog.enums.Language;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, Long> {


	 Optional<PostContent> findByUrlHandleAndLanguage(String urlHandle, Language language);

	 Optional<PostContent> findByPostAndLanguage(Post post, Language language);
	 
	 boolean existsByUrlHandleAndLanguage(String urlHandle, Language language);
	 
	 boolean existsByPostIdAndLanguage(Long postId, Language language);
}