package com.devnoir.electricdreams.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.BlogPostDTO;
import com.devnoir.electricdreams.entities.Post;
import com.devnoir.electricdreams.repositories.PostRepository;

@Service
public class BlogPostService {

	@Autowired
 	private PostRepository postRepository;
 	
 	@Transactional(readOnly = true)
 	public Page<BlogPostDTO> findAllPaged(Pageable pageable) {
 		Page<Post> page = postRepository.findAll(pageable);
 		return page.map(x -> new BlogPostDTO(x));
 	}

}