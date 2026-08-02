package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.repository.PanierRepository;   // ← Important : PanierItemRepository
import com.fify.fyGYM.repository.PanierRepository;
import com.fify.fyGYM.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private PanierRepository panierRepository;   // ← Correction ici

    // ===================== LISTE =====================
    public List<Produit> listeProduit() {
        return produitRepository.findAll();
    }

    // ===================== CRÉATION =====================
    public void createProduit(Produit produit, MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                produit.setImage(file.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
        produitRepository.save(produit);
    }

    // ===================== MODIFICATION =====================
    public void editeProduit(Long id, MultipartFile file, Produit newProduit) {
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        existing.setNom(newProduit.getNom());
        existing.setDescription(newProduit.getDescription());
        existing.setPrix(newProduit.getPrix());
        existing.setStock(newProduit.getStock());
        existing.setCategorie(newProduit.getCategorie());
        existing.setNote(newProduit.getNote());

        if (file != null && !file.isEmpty()) {
            try {
                existing.setImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la mise à jour de l'image", e);
            }
        }

        produitRepository.save(existing);
    }

    // ===================== SUPPRESSION =====================
    @Transactional
    public void deletProduit(Long id) {
        // Supprime d'abord les articles dans le panier
        panierRepository.deleteByProduitId(id);

        // Ensuite supprime le produit
        produitRepository.deleteById(id);
    }

    // ===================== RECHERCHE =====================
    public Produit findOneProduit(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    }
}