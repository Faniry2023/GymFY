package com.fify.fyGYM.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire")
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    private String contenu;
    private LocalDateTime dateCommentaire = LocalDateTime.now();

    // ── Getters / Setters ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    public void setDateCommentaire(LocalDateTime d) { this.dateCommentaire = d; }
}