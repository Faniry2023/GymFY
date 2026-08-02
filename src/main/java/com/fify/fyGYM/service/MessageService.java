// MessageService.java
package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Message;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.MessageRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    @Autowired private MessageRepository messageRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;

    public MessageService(MessageRepository messageRepository , UtilisateurRepository utilisateurRepository){
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Envoyer un message
    public void envoyer(Long userId, String contenu, String expediteur) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        Message msg = new Message();
        msg.setUtilisateur(u);
        msg.setContenu(contenu);
        msg.setExpediteur(expediteur);
        messageRepository.save(msg);
    }

    // Liste des messages d'un user
    public List<Message> getMessages(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return messageRepository.findByUtilisateurOrderByDateEnvoiAsc(u);
    }

    // Notifications non lues pour l'user (messages de l'admin)
    public long countNonLusUser(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return messageRepository
                .countByUtilisateurAndExpediteurAndLuFalse(u, "ADMIN");
    }

    // Notifications non lues pour l'admin (messages des users)
    public long countNonLusAdmin() {
        return messageRepository.countByExpediteurAndLuFalse("USER");
    }

    // Marquer les messages comme lus
    public void marquerLus(Long userId, String expediteur) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        List<Message> msgs = messageRepository
                .findByUtilisateurOrderByDateEnvoiAsc(u);
        msgs.stream()
                .filter(m -> m.getExpediteur().equals(expediteur) && !m.isLu())
                .forEach(m -> { m.setLu(true); messageRepository.save(m); });
    }

    // Tous les users qui ont envoyé un message
    public List<Utilisateur> getUsersAvecMessages() {
        return messageRepository.findAll().stream()
                .filter(m -> m.getExpediteur().equals("USER"))
                .map(Message::getUtilisateur)
                .distinct()
                .toList();
    }

    // Nombre de messages non lus d'un user spécifique
    public long countNonLusParUser(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return messageRepository
                .countByUtilisateurAndExpediteurAndLuFalse(u, "USER");
    }
}