package com.devnoir.electricdreams.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}