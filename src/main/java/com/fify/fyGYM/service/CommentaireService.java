package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Commentaire;
import com.fify.fyGYM.model.Notification;
import com.fify.fyGYM.model.Publication;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.CommentaireRepository;
import com.fify.fyGYM.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Commentaire ajouterCommentaire(Commentaire commentaire) {
        // Sauvegarde du commentaire
        Commentaire saved = commentaireRepository.save(commentaire);

        // ✅ Création de la notification
        Publication pub = saved.getPublication();
        if (pub != null && pub.getUtilisateur() != null) {
            Utilisateur proprietaire = pub.getUtilisateur();
            Utilisateur commentateur = saved.getUtilisateur();

            // Ne pas notifier si c'est l'auteur qui commente sa propre publication
            if (!proprietaire.getId().equals(commentateur.getId())) {
                Notification notif = new Notification();   // Ton entité personnalisée
                notif.setDestinataire(proprietaire);
                notif.setCommentaire(saved);
                notif.setMessage(commentateur.getNom() + " " + commentateur.getPrenom()
                        + " a commenté votre publication.");
                notif.setLu(false);
                notificationRepository.save(notif);
            }
        }

        return saved;
    }
}