// MessageRepository.java
package com.fify.fyGYM.repository;
import com.fify.fyGYM.model.Message;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUtilisateurOrderByDateEnvoiAsc(Utilisateur utilisateur);
    // Compte les messages non lus de l'ADMIN vers cet user
    long countByUtilisateurAndExpediteurAndLuFalse(Utilisateur u, String expediteur);
    // Compte les messages non lus de tous les users vers ADMIN
    long countByExpediteurAndLuFalse(String expediteur);

}