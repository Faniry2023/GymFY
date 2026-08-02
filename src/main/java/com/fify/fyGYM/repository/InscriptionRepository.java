package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Utilisateur;
import jdk.jshell.execution.Util;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Utilisateur , Long> {

}
