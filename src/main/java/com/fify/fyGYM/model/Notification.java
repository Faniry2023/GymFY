package com.fify.fyGYM.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @ManyToOne
    @JoinColumn(name = "commentaire_id")
    private Commentaire commentaire;

    private String message;

    private boolean lu = false;

    private LocalDateTime dateNotification = LocalDateTime.now();

    // ==================== CONSTRUCTEURS ====================
    public Notification() {}

    // ==================== GETTERS & SETTERS ====================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
    }

    public Commentaire getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(Commentaire commentaire) {
        this.commentaire = commentaire;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(LocalDateTime dateNotification) {
        this.dateNotification = dateNotification;
    }
}