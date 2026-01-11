package com.devnoir.electricdreams.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devnoir.electricdreams.dto.TagDTO;
import com.devnoir.electricdreams.entities.Tag;
import com.devnoir.electricdreams.enums.Language;
import com.devnoir.electricdreams.repositories.TagRepository;
import com.devnoir.electricdreams.services.exceptions.BusinessException;
import com.devnoir.electricdreams.services.exceptions.DatabaseException;
import com.devnoir.electricdreams.services.exceptions.ResourceNotFoundException;

@Service
public class AdminTagService {

	@Autowired
	private TagRepository tagRepository;
	
	@Transactional(readOnly = true)
	public List<TagDTO> findAllByLanguage(String language) {
		try {
	        Language lang = Language.valueOf(language.toUpperCase());
	        return tagRepository.findByLanguage(lang).stream()
	                .map(TagDTO::new)
	                .collect(Collectors.toList());
	    } catch (IllegalArgumentException e) {
	        throw new BusinessException("Invalid language: " + language);
	    }
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
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		try {
            Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
            
            if (!tag.getContents().isEmpty()) {
                throw new BusinessException("Cannot delete tag that is being used in posts");
            }
            
            tagRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Database integrity violation");
        }
	}
	
	
    
    private void copyDtoToEntity(TagDTO dto, Tag tag) {
        tag.setName(dto.getName());
        tag.setLanguage(Language.valueOf(dto.getLanguage().toUpperCase()));
    }
    
    public Tag getOrCreateEntity(TagDTO dto) {
        if (dto.isNew()) {
            Tag tag = new Tag();
            copyDtoToEntity(dto, tag);
            return tagRepository.save(tag);
        } else {
            return tagRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + dto.getId()));
        }
    }
}