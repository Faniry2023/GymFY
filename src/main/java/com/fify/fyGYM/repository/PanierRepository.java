package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<PanierItem, Long> {

        List<PanierItem> findByUtilisateur(Utilisateur utilisateur);
        Optional<PanierItem> findByUtilisateurAndProduit(Utilisateur utilisateur, Produit produit);

        @Transactional
        void deleteByUtilisateur(Utilisateur utilisateur);

        // ✅ Méthode CORRECTE ici
        @Transactional
        void deleteByProduitId(Long produitId);
}