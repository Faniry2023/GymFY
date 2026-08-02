package com.fify.fyGYM.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String numTel;
    private String comfirm_mdp;

    // ✅ AJOUTE CE CHAMP
    private String role;   // "USER" ou "ADMIN"

    private LocalDateTime dateInscription = LocalDateTime.now();

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }

    public String getComfirm_mdp() { return comfirm_mdp; }
    public void setComfirm_mdp(String comfirm_mdp) { this.comfirm_mdp = comfirm_mdp; }

    // ✅ Getter & Setter pour le rôle
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}