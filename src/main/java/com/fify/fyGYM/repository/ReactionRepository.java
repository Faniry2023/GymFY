package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Publication;
import com.fify.fyGYM.model.Reaction;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    // Réaction d'un user sur une publication
    Optional<Reaction> findByPublicationAndUtilisateur(
            Publication pub, Utilisateur user);

    // Compte par emoji sur une publication
    long countByPublicationAndEmoji(Publication pub, String emoji);
}