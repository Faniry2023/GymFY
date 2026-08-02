package com.fify.fyGYM.service;

import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.PanierRepository;
import com.fify.fyGYM.repository.ProduitRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PanierService {

    @Autowired private PanierRepository panierRepository;
    @Autowired private ProduitRepository produitRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;


    // ── Ajouter au panier (ou augmenter quantité) ──
    public int ajoutPanier(Long produitId, Long utilisateurId) {

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("utilisateur non connecté"));

        if (produit.getStock() <= 0) {
            throw new RuntimeException("Stock épuisé");
        }

        // Diminue le stock
        produit.setStock(produit.getStock() - 1);
        produitRepository.save(produit);

        Optional<PanierItem> existant = panierRepository.findByUtilisateurAndProduit(utilisateur , produit);
        if(existant.isPresent()){
            PanierItem item = existant.get();
            item.setQuantite(item.getQuantite() + 1);
            panierRepository.save(item);
        }else{
            PanierItem newItem = new PanierItem();
            newItem.setProduit(produit);
            newItem.setQuantite(1);
            newItem.setUtilisateur(utilisateur);
            panierRepository.save(newItem);
        }
        return produit.getStock();
    }

    // ── Lister le panier d'un utilisateur ──
    public List<PanierItem> getPanierUti(Long utilisateurId){
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé!"));
        return panierRepository.findByUtilisateur(utilisateur);
    }

    //supprimé produit de panier
    public void deletePoduitPanier(Long panierItemId , Long utilisateurId){
            PanierItem item = panierRepository.findById(panierItemId)
                    .orElseThrow(() -> new RuntimeException("Item non trouvé!"));
            Produit produit = item.getProduit();
            produit.setStock(produit.getStock() + item.getQuantite());
            produitRepository.save(produit);
            panierRepository.deleteById(panierItemId);

    }

    //modifier le quantité
    public void modifierQuantite(Long panierItemId, int nouvelleQuantite, Long utilisateurId) {
        PanierItem item = panierRepository.findById(panierItemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));

        int ancienneQuantite = item.getQuantite();
        int difference = nouvelleQuantite - ancienneQuantite;

        Produit produit = item.getProduit();

        if (difference > 0 && produit.getStock() < difference) {
            throw new RuntimeException("Stock insuffisant");
        }

        // Ajuste le stock
        produit.setStock(produit.getStock() - difference);
        produitRepository.save(produit);

        item.setQuantite(nouvelleQuantite);
        panierRepository.save(item);
    }

    //vider la panier
    public void viderPanier(Long utilisateurId){
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        panierRepository.deleteByUtilisateur(utilisateur);
    }
}