package com.fify.fyGYM.controller;

import com.fify.fyGYM.helper.CommandeHelper;
import com.fify.fyGYM.helper.ProduitComHelper;
import com.fify.fyGYM.model.*;
import com.fify.fyGYM.repository.CommandeRepository;
import com.fify.fyGYM.repository.PanierRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import com.fify.fyGYM.service.ProduitService;
import com.fify.fyGYM.service.PublicationService;
import com.fify.fyGYM.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/produit")
public class ProduitController {
    @Autowired
    private ProduitService produitService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private PublicationService publicationService;
    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    public ProduitController(ProduitService produitService,
                             UtilisateurRepository utilisateurRepository,
                             PublicationService publicationService,
                             PanierRepository panierRepository,
                             UtilisateurService utilisateurService) {
        this.produitService      = produitService;
        this.utilisateurRepository = utilisateurRepository;
        this.publicationService  = publicationService;
        this.panierRepository = panierRepository;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/ajoutProduit")
    public String getPageAjout() {
        return "ajoutProduitPage";
    }

    @GetMapping("/acceuil")
    public String getPageAcceuil(Model model, HttpSession session) {
        model.addAttribute("produits",     produitService.listeProduit());

        List<Publication> publications = publicationService.getTout();
        model.addAttribute("publications", publications);

        // ✅ Réaction du user connecté pour chaque pub
        Long userId = (Long) session.getAttribute("utilisateurId");
        Map<Long, String> userReactions = new HashMap<>();
        if (userId != null) {
            model.addAttribute("isLogged",true);
            publications.forEach(pub -> {
                String r = publicationService.getReactionUser(pub.getId(), userId);
                if (r != null) userReactions.put(pub.getId(), r);
            });
        }else{
            model.addAttribute("isLogged",false);

        }
        model.addAttribute("userReactions", userReactions);
        return "acceuilPage";
    }

    @PostMapping("/ajoutProduit")
    public String ajoutNewProduit(@ModelAttribute Produit produit,
                                  @RequestParam("imageFile") MultipartFile file) {
        produitService.createProduit(produit, file);
        return "redirect:afficheAllProduit?page=produits";
    }

    @GetMapping("/afficheAllProduit")
    public String getAllProduit(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/api/inscri/login";
        }
        List<Commande> commandes = new ArrayList<Commande>();
        commandes = commandeRepository.findAll();
        List<Long> userAdd = new ArrayList<Long>();
        List<CommandeHelper> commandeHelpers = new ArrayList<CommandeHelper>();
        for(Commande c : commandes){
            if(!userAdd.contains(c.getId_user())){
                userAdd.add(c.getId_user());
                int prixTotaux = 0;
                //filter_commande.add(c);
                Utilisateur utilisateur = new Utilisateur();
                utilisateur = utilisateurService.findById(c.getId_user());
                List<ProduitComHelper> prodHelp = new ArrayList<ProduitComHelper>();
                List<PanierItem> panierItems = new ArrayList<PanierItem>();
                panierItems = panierRepository.findByUtilisateur(utilisateur);
                    for (PanierItem pi: panierItems){
                        int qte = pi.getQuantite();
                        Produit produit = new Produit();
                        produit = produitService.findOneProduit(pi.getProduit().getId());
                        int prix = produit.getPrix() * qte;
                        ProduitComHelper pch = new ProduitComHelper();
                        pch.setProduit(produit);
                        pch.setPrix(prix);
                        pch.setQuantite(qte);
                        prodHelp.add(pch);
                        prixTotaux += prix;
                    }

                CommandeHelper ch = new CommandeHelper();
                ch.setUtilisateur(utilisateur);
                ch.setProduits(prodHelp);
                ch.setPrixTotaux(prixTotaux);
                ch.setArticl(prodHelp.size());
                ch.setCommande(c);
                commandeHelpers.add(ch);
            }
        }

        model.addAttribute("commandeHelpers",     commandeHelpers);
        model.addAttribute("produits",      produitService.listeProduit());
        model.addAttribute("utilisateurs",  utilisateurRepository.findAll());
        return "adminPage";
    }

    @GetMapping("/imageProduit/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImageProduit(@PathVariable Long id) {
        Produit produit = produitService.findOneProduit(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(produit.getImage());
    }

    @GetMapping("/pageEdite/{id}")
    public String afficheProduitEdite(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produitService.findOneProduit(id));
        return "editePage";
    }

    @PostMapping("/editeProduit/{id}")
    public String newProduitEdit(@ModelAttribute Produit newProduit,
                                 @PathVariable Long id,
                                 @RequestParam("imageFile") MultipartFile file) {
        produitService.editeProduit(id, file, newProduit);
        return "redirect:/api/produit/afficheAllProduit?page=produits";
    }

    @PostMapping("/supprimeUnProduit/{id}")
    public String deleteProduit(@PathVariable Long id) {
        produitService.deletProduit(id);
        return "redirect:/api/produit/afficheAllProduit?page=produits";
    }

    @GetMapping("/admin/utilisateurs/count")
    @ResponseBody
    public Map<String, Object> getTotalUtilisateurs() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", utilisateurRepository.count());
        return response;
    }


    @Transactional
    @PostMapping("/admin/utilisateur/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id, HttpSession session) {
        Long currentAdminId = (Long) session.getAttribute("utilisateurId");

        Utilisateur user = utilisateurRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/api/produit/afficheAllProduit?page=utilisateurs&error=notfound";
        }

        if ("ADMIN".equals(user.getRole())) {
            long adminCount = utilisateurRepository.countByRole("ADMIN");
            if (adminCount <= 1) {
                return "redirect:/api/produit/afficheAllProduit?page=utilisateurs&error=lastadmin";
            }
        }

        if (id.equals(currentAdminId)) {
            return "redirect:/api/produit/afficheAllProduit?page=utilisateurs&error=selfdelete";
        }

        utilisateurRepository.deleteById(id);
        return "redirect:/api/produit/afficheAllProduit?page=utilisateurs&success=deleted";
    }

}
