package com.fify.fyGYM.service;

import com.fify.fyGYM.model.*;
import com.fify.fyGYM.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class PublicationService {

    @Autowired private PublicationRepository  publicationRepository;
    @Autowired private CommentaireRepository  commentaireRepository;
    @Autowired private UtilisateurRepository  utilisateurRepository;
    @Autowired private ReactionRepository     reactionRepository;

    // Publier
    public void publier(Long userId, MultipartFile photoAvant,
                        MultipartFile photoApres, String legende) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        Publication pub = new Publication();
        pub.setUtilisateur(u);
        pub.setLegende(legende);
        try {
            if (photoAvant != null && !photoAvant.isEmpty())
                pub.setPhotoAvant(photoAvant.getBytes());
            if (photoApres != null && !photoApres.isEmpty())
                pub.setPhotoApres(photoApres.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur photo");
        }
        publicationRepository.save(pub);
    }

    public void supprimer(Long pubId) {
        publicationRepository.deleteById(pubId);
    }

    // Publications d'un user (pour coaching — modification/suppression)
    public List<Publication> getParUser(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return publicationRepository.findByUtilisateur(u);
    }

    // Toutes les publications (page accueil)
    public List<Publication> getTout() {
        return publicationRepository.findAllByOrderByDatePublicationDesc();
    }

    // Commenter
    public void commenter(Long pubId, Long userId, String contenu) {
        Publication pub = publicationRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        Commentaire c = new Commentaire();
        c.setPublication(pub);
        c.setUtilisateur(u);
        c.setContenu(contenu);
        commentaireRepository.save(c);
    }

    // ✅ Réaction avec règle : 1 seule réaction par user
    public Map<String, Object> reagir(Long pubId, Long userId, String emoji) {
        Publication pub = publicationRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));

        Optional<Reaction> existing =
                reactionRepository.findByPublicationAndUtilisateur(pub, u);

        String ancienEmoji = null;
        boolean removed    = false;

        if (existing.isPresent()) {
            Reaction r = existing.get();
            ancienEmoji = r.getEmoji();

            if (r.getEmoji().equals(emoji)) {
                // ✅ Même emoji → toggle OFF (supprime)
                reactionRepository.delete(r);
                removed = true;
            } else {
                // ✅ Emoji différent → change la réaction
                r.setEmoji(emoji);
                reactionRepository.save(r);
            }
        } else {
            // ✅ Pas encore de réaction → crée
            Reaction r = new Reaction();
            r.setPublication(pub);
            r.setUtilisateur(u);
            r.setEmoji(emoji);
            reactionRepository.save(r);
        }

        // Retourne les compteurs mis à jour + état user
        Map<String, Object> result = new HashMap<>();
        result.put("muscle", reactionRepository.countByPublicationAndEmoji(pub, "muscle"));
        result.put("feu",    reactionRepository.countByPublicationAndEmoji(pub, "feu"));
        result.put("bravo",  reactionRepository.countByPublicationAndEmoji(pub, "bravo"));
        result.put("coeur",  reactionRepository.countByPublicationAndEmoji(pub, "coeur"));
        result.put("userEmoji",   removed ? null : emoji);
        result.put("ancienEmoji", ancienEmoji);
        result.put("removed",     removed);
        return result;
    }

    // Compteurs d'une publication
    public Map<String, Long> getCompteurs(Long pubId) {
        Publication pub = publicationRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        Map<String, Long> result = new HashMap<>();
        result.put("muscle", reactionRepository.countByPublicationAndEmoji(pub, "muscle"));
        result.put("feu",    reactionRepository.countByPublicationAndEmoji(pub, "feu"));
        result.put("bravo",  reactionRepository.countByPublicationAndEmoji(pub, "bravo"));
        result.put("coeur",  reactionRepository.countByPublicationAndEmoji(pub, "coeur"));
        return result;
    }

    // Réaction du user connecté sur une pub
    public String getReactionUser(Long pubId, Long userId) {
        Publication pub = publicationRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return reactionRepository.findByPublicationAndUtilisateur(pub, u)
                .map(Reaction::getEmoji).orElse(null);
    }

    public byte[] getPhotoAvant(Long pubId) {
        return publicationRepository.findById(pubId)
                .map(Publication::getPhotoAvant).orElse(null);
    }

    public byte[] getPhotoApres(Long pubId) {
        return publicationRepository.findById(pubId)
                .map(Publication::getPhotoApres).orElse(null);
    }
    // Dans PublicationService.java — ajouter
    public Publication findById(Long pubId) {
        return publicationRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
    }
}