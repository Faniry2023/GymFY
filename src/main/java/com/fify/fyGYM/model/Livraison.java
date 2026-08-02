package com.fify.fyGYM.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livraison")
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nom;
    private Long totalPrice;
    private String code_liv;
    private boolean ok;

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getNom(){return nom;}
    public void setNom(String nom){this.nom = nom;}

    public Long getTotalPrice(){return totalPrice;}
    public void setTotalPrice(Long totalPrice){this.totalPrice = totalPrice;}

    public String getCode_liv(){return code_liv;}
    public void setCode_liv(String code_liv){this.code_liv = code_liv;}

    public boolean getOk(){return ok;}
    public void setOk(boolean ok){this.ok = ok;}
}
