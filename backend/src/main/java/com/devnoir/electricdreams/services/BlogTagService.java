package com.devnoir.electricdreams.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.TagRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;

@Service
public class BlogTagService {

	@Autowired
	private TagRepository tagRepository;
	
	@Transactional(readOnly = true)
	public Page<TagDTO> findAllByLanguage(String language, Pageable pageable) {
		try {
            Language lang = Language.valueOf(language.toUpperCase());
            Page<Tag> page = tagRepository.findAllByLanguage(lang, pageable);
            return page.map(x -> new TagDTO(x));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid language: " + language);
        }
    }
}