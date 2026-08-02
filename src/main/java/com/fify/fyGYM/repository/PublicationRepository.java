package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Publication;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PublicationRepository
        extends JpaRepository<Publication, Long> {

    // ✅ Un user peut avoir PLUSIEURS publications
    List<Publication> findByUtilisateur(Utilisateur utilisateur);

    // Toutes les publications triées par date
    List<Publication> findAllByOrderByDatePublicationDesc();
}