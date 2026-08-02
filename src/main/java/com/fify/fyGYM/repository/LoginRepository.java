package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Admin;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Utilisateur,Long> {
    Optional<Utilisateur> findByEmailAndMdp(String email , String mdp);
}
