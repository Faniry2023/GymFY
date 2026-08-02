package com.fify.fyGYM.helper;

import com.fify.fyGYM.model.Produit;

public class ProduitComHelper {
    private Produit produit;
    private int quantite;
    private int prix;

    public Produit getProduit(){
        return produit;
    }
    public  void setProduit(Produit produit){
        this.produit = produit;
    }

    public int getQuantite(){
        return  quantite;
    }
    public void setQuantite(int quantite){
        this.quantite = quantite;
    }

    public int getPrix(){
        return prix;
    }
    public void setPrix(int prix){
        this.prix = prix;
    }
}
