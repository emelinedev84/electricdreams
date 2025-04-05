package com.devnoir.electricdreams.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.PostContentDTO;
import com.devnoir.electricdreams.entities.PostContent;
import com.devnoir.electricdreams.repositories.PostContentRepository;

@Service
public class PostContentService {
	
 	@Autowired
 	private PostContentRepository contentRepository;
 	
 	@Transactional(readOnly = true)
 	public Page<PostContentDTO> findAllPaged(Pageable pageable) {
 		Page<PostContent> page = contentRepository.findAll(pageable);
 		return page.map(x -> new PostContentDTO(x));
 	}
}