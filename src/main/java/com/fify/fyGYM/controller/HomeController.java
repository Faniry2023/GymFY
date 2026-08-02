package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Produit;
import com.fify.fyGYM.model.Publication;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.UtilisateurRepository;
import com.fify.fyGYM.service.ProduitService;
import com.fify.fyGYM.service.PublicationService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ProduitService produitService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PublicationService publicationService;

    public HomeController(ProduitService produitService,
                             UtilisateurRepository utilisateurRepository,
                             PublicationService publicationService) {
        this.produitService      = produitService;
        this.utilisateurRepository = utilisateurRepository;
        this.publicationService  = publicationService;
    }

    @GetMapping
    public String getPageAcceuil(Model model, HttpSession session) {
        model.addAttribute("produits",     produitService.listeProduit());

        List<Publication> publications = publicationService.getTout();
        model.addAttribute("publications", publications);

        // ✅ Réaction du user connecté pour chaque pub
        Long userId = (Long) session.getAttribute("utilisateurId");
        Map<Long, String> userReactions = new HashMap<>();
        if (userId != null) {
            model.addAttribute("isLogged",false);
            publications.forEach(pub -> {
                String r = publicationService.getReactionUser(pub.getId(), userId);
                if (r != null) userReactions.put(pub.getId(), r);
            });
            model.addAttribute("isLogged",true);
        }else{
            model.addAttribute("isLogged",false);
        }
        model.addAttribute("userReactions", userReactions);
        return "acceuilPage";
    }

}