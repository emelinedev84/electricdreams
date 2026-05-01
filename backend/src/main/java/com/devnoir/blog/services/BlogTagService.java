package com.devnoir.blog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.PublicPostSummaryDTO;
import com.devnoir.blog.dto.TagPublicDTO;
import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.TagRepository;

@Service
public class BlogTagService {

	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private BlogService blogService;
	
	@Transactional(readOnly = true)
    public List<TagPublicDTO> findAll(Language language) {
        return tagRepository.findAll().stream()
                .map(c -> new TagPublicDTO(
                        c.getCode(),
                        c.getLocalizedName(language)  // ou helper
                ))
                .toList();
    }
	
	@Transactional(readOnly = true)
	public Page<PublicPostSummaryDTO> findPostsByTag(String language, String tagCode, Pageable pageable) {
		return blogService.findPublicPostsByTag(language, tagCode, pageable);
	}
}