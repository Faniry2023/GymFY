package com.fify.fyGYM.helper;

import com.fify.fyGYM.model.Commande;
import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.model.Utilisateur;

import java.util.List;

public class CommandeHelper {
    private Utilisateur utilisateur;
    private List<ProduitComHelper> produits;
    private int prixTotaux;
    private int articl;
    private Commande commande;

    public Utilisateur getUtilisateur(){return utilisateur;}
    public void setUtilisateur(Utilisateur utilisateur){this.utilisateur = utilisateur;}

    public List<ProduitComHelper> getProduits(){return produits;}
    public void setProduits(List<ProduitComHelper> produits){this.produits = produits;}

    public  int getPrixTotaux(){return prixTotaux;}
    public void setPrixTotaux(int prixTotaux){this.prixTotaux = prixTotaux;}

    public int getArticl(){return articl;}
    public void setArticl(int articl){this.articl = articl;}

    public Commande getCommande(){return commande;}
    public void setCommande(Commande commande){this.commande = commande;}

}
