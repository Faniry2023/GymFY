package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.service.PanierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/panier")
public class PanierController {

    @Autowired
    private PanierService panierService;

    // ── Page panier (Thymeleaf) ──
    @GetMapping("/monPanier")
    public String getPanierUti(Model model , HttpSession session){
        Long userId = (Long) session.getAttribute("utilisateurId");
        if(userId == null){
            return "redirect:/api/inscri/login";
        }
        model.addAttribute("panierItem",panierService.getPanierNonPayer(userId));
        return "panierPage";
    }

    // ── Ajouter au panier (appelé depuis acceuilPage) ──
    @PostMapping("/ajoutPanier/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ajoutProduitPanier(
            @PathVariable Long id, HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("utilisateurId");
        if(userId == null){
            response.put("succes",false);
            response.put("message","utilisateur non connecter");
            return ResponseEntity.status(401).body(response);
        }

        try {
            int nouveauStock = panierService.ajoutPanier(id, userId);
            response.put("succes", true);
            response.put("nouveauStock", nouveauStock);
            response.put("message", "Produit ajouté au panier !");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    //supprimé un produit de l'item
    @PostMapping("/deleteProduitItem/{panierItemId}")
    public String deletePanierItem(@PathVariable Long panierItemId , HttpSession session){
        Long utilisateurId = (Long) session.getAttribute("utilisateurId");
        panierService.deletePoduitPanier(panierItemId , utilisateurId);
        return "redirect:/api/panier/monPanier";
    }

    @PostMapping("/modifierQuantite/{panierItemId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> modifierQuantite(
            @PathVariable Long panierItemId,
            @RequestParam int quantite,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("utilisateurId");

        try {
            panierService.modifierQuantite(panierItemId, quantite, userId);
            response.put("succes", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



}