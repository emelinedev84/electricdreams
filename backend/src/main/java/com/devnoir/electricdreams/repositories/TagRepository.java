package com.devnoir.electricdreams.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

}
