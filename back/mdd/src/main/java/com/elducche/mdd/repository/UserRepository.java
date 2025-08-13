package com.elducche.mdd.repository;

import com.elducche.mdd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository pour l'entité User
 * 
 * Fournit les opérations CRUD standard et des méthodes de recherche
 * optimisées pour l'authentification et la gestion des utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Trouve un utilisateur par son email
     * @param email L'email de l'utilisateur
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Trouve un utilisateur par son username
     * @param username Le nom d'utilisateur
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Vérifie si un email existe déjà
     * @param email L'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifie si un username existe déjà
     * @param username Le username à vérifier
     * @return true si le username existe
     */
    boolean existsByUsername(String username);
    
    /**
     * Trouve un utilisateur par email ou username
     * @param identifier Email ou username
     * @return Optional contenant l'utilisateur s'il existe
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);
}
