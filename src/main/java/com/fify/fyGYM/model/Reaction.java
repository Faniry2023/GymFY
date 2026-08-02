package com.fify.fyGYM.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reaction",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"publication_id", "utilisateur_id"}
        ))
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // "muscle", "feu", "bravo", "coeur"
    private String emoji;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Publication getPublication() { return publication; }
    public void setPublication(Publication p) { this.publication = p; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur u) { this.utilisateur = u; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
}