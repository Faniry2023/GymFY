package com.fify.fyGYM.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PanierItem {

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setEstPayer(boolean estPayer){ this.estPayer = estPayer;}
    public boolean getEstPayer(){return estPayer;}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers l'utilisateur propriétaire du panier
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Lien vers le produit
    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // Quantité ajoutée
    private int quantite;

    // ✅ Après
    @Column(columnDefinition = "boolean default false")
    private boolean estPayer = false;
}