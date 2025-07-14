package com.devnoir.electricdreams.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    // NOVAS CONSULTAS PARA VALIDAÇÃO
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Consulta para verificar se email existe (exceto para um ID específico)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);
    
    // Consulta para verificar se username existe (exceto para um ID específico)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);
}