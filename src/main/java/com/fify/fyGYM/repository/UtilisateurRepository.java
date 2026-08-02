package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    long countByRole(String role);

    // Si tu veux chercher par email (utile pour login)
    Optional<Utilisateur> findByEmail(String email);
    long count();

}