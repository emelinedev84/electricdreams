package com.devnoir.blog.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.blog.dto.TagDTO;
import com.devnoir.blog.entities.Post;
import com.devnoir.blog.entities.Tag;
import com.devnoir.blog.repositories.TagRepository;
import com.devnoir.blog.services.exceptions.DatabaseException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;
import com.devnoir.blog.utils.SlugifyHelper;

@Service
public class AdminTagService {

	@Autowired
	private TagRepository tagRepository;
	
	@Transactional(readOnly = true)
	public Page<TagDTO> findAllPaged(Pageable pageable) {
		Page<Tag> page = tagRepository.findAll(pageable);
		return page.map(x -> new TagDTO(x));
	}
	
	@Transactional(readOnly = true)
	public TagDTO findById(Long id) {
		Optional<Tag> optional = tagRepository.findById(id);
        Tag tag = optional.orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));
        return new TagDTO(tag);
	}
	
	@Transactional
	public TagDTO create(TagDTO dto) {
		Tag tag = new Tag();
		copyDtoToEntity(dto, tag);
		tag = tagRepository.save(tag);
		return new TagDTO(tag);
	}
	
	@Transactional
	public TagDTO update(Long id, TagDTO dto) {
		Tag tag = tagRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
		copyDtoToEntity(dto, tag);
		tag = tagRepository.save(tag);
		return new TagDTO(tag);
	}
	
	@Transactional
	public void delete(Long id) {
		try {
            Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
            Set<Post> posts = new HashSet<>(tag.getPosts());
            posts.forEach(post -> post.getTags().remove(tag));
            tagRepository.delete(tag);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Database integrity violation");
        }
	}
	    
    private void copyDtoToEntity(TagDTO dto, Tag tag) {
    	if (dto.getCode() == null || dto.getCode().isBlank()) {
    		tag.setCode(SlugifyHelper.toSlug(dto.getNameEn()));
    	} else {
    		tag.setCode(SlugifyHelper.toSlug(dto.getCode()));
    	}
    	tag.setNameEn(dto.getNameEn());
    	tag.setNamePt(dto.getNamePt());
    }
}