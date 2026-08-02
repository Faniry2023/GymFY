package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Modifying
    @Query(value = "DELETE FROM ligne_panier WHERE produit_id = :produitId", nativeQuery = true)
    void deleteLignesPanierByProduitId(@Param("produitId") Long produitId);

    @Modifying
    @Query(value = "DELETE FROM panier_item WHERE produit_id = :produitId", nativeQuery = true)
    void deletePanierItemsByProduitId(@Param("produitId") Long produitId);
}
