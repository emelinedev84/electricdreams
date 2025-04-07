package com.devnoir.electricdreams.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Category;
import com.devnoir.electricdreams.enums.Language;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Page<Category> findByLanguage(Language lang, Pageable pageable);

}
