package com.fify.fyGYM.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coaching")
public class Coaching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers l'utilisateur
    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    private double taille;   // en cm
    private double poids;    // en kg
    private int age;
    private String genre;    // "HOMME" ou "FEMME"

    @Lob
    private byte[] photo;    // photo de profil (optionnelle)

    // ── Getters / Setters ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public double getTaille() { return taille; }
    public void setTaille(double taille) { this.taille = taille; }
    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
}