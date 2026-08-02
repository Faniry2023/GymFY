package com.fify.fyGYM.controller;

import com.fify.fyGYM.helper.CommandeHelper;
import com.fify.fyGYM.helper.ProduitComHelper;
import com.fify.fyGYM.model.Commande;
import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.CommandeRepository;
import com.fify.fyGYM.repository.PanierRepository;
import com.fify.fyGYM.service.ProduitService;
import com.fify.fyGYM.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class CommandeApiController {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private ProduitService produitService;

    @GetMapping("/commande-details/{idUser}")
    public ResponseEntity<CommandeHelper> getDetails(@PathVariable Long idUser) {

        Commande commande = commandeRepository.findByIdUser(idUser).stream()
                .findFirst()
                .orElse(null);

        if (commande == null) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateur = utilisateurService.findById(idUser);

        List<PanierItem> panierItems = panierRepository.findByUtilisateur(utilisateur);
        List<ProduitComHelper> prodHelp = new ArrayList<>();
        int prixTotaux = 0;

        for (PanierItem pi : panierItems) {
            int qte = pi.getQuantite();
            Produit produit = produitService.findOneProduit(pi.getProduit().getId());
            int prix = produit.getPrix() * qte;

            ProduitComHelper pch = new ProduitComHelper();
            pch.setProduit(produit);
            pch.setQuantite(qte);
            pch.setPrix(prix);
            prodHelp.add(pch);

            prixTotaux += prix;
        }

        CommandeHelper ch = new CommandeHelper();
        ch.setUtilisateur(utilisateur);
        ch.setProduits(prodHelp);
        ch.setPrixTotaux(prixTotaux);
        ch.setArticl(prodHelp.size());
        ch.setCommande(commande);

        return ResponseEntity.ok(ch);
    }
}
