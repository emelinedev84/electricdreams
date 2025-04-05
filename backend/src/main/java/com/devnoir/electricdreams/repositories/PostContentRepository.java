package com.devnoir.electricdreams.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.enums.Language;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, Long> {


	 Optional<PostContent> findByUrlHandleAndLanguage(String urlHandle, Language language);

	 Optional<PostContent> findByPostAndLanguage(Post post, Language language);
}