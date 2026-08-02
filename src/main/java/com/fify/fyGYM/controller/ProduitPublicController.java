package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.service.ProduitService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/produits")
public class ProduitPublicController {

    @Autowired
    private ProduitService produitService;

    // ===================== PAGE PRINCIPALE DES PRODUITS =====================
    @GetMapping
    public String pageProduits(Model model, HttpSession session) {
        List<Produit> produits = produitService.listeProduit();

        model.addAttribute("produits", produits);
        model.addAttribute("utilisateurId", session.getAttribute("utilisateurId")); // pour savoir si connecté
        Long userId = (Long) session.getAttribute("utilisateurId");
        if(userId == null){
            model.addAttribute("isLogged",false);
        }else{
            model.addAttribute("isLogged",true);
        }
        return "produitPage";   // → produits.html
    }

    // ===================== DÉTAIL D'UN PRODUIT (Optionnel) =====================
    @GetMapping("/{id}")
    public String detailProduit(@PathVariable Long id, Model model) {
        Produit produit = produitService.findOneProduit(id);
        model.addAttribute("produit", produit);
        return "produit-detail";   // tu peux créer ce template plus tard
    }
}