package com.devnoir.electricdreams.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devnoir.electricdreams.entities.User;
import com.devnoir.electricdreams.projections.UserDetailsProjection;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query(nativeQuery = true, value = """ 
    		SELECT tb_user.username AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
    		FROM tb_user
    		INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
    		INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
    		WHERE tb_user.username = :username
    	    """)
    List<UserDetailsProjection> searchUserAndRolesByUsername(String username);
    
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