package com.fify.fyGYM.paysim.controler;

import com.fify.fyGYM.model.Commande;
import com.fify.fyGYM.model.Livraison;
import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.paysim.model.InfoPaiDevHelper;
import com.fify.fyGYM.paysim.model.ValueQr;
import com.fify.fyGYM.repository.CommandeRepository;
import com.fify.fyGYM.paysim.service.PaySimService;
import com.fify.fyGYM.repository.UtilisateurRepository;
import com.fify.fyGYM.service.EmailService;
import com.fify.fyGYM.service.LivraisonService;
import com.fify.fyGYM.service.PanierService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Controller
public class PaySimController {

    @Autowired
    private PaySimService paySimService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PanierService panierService;

    @Autowired
    private LivraisonService livraisonService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CommandeRepository commandeRepository;

    @PostMapping("/setup")
    public String setup(
            @RequestParam String nom,
            @RequestParam String tel,
            @RequestParam String lieu,
            @RequestParam String latitude,
            @RequestParam String longitude,
            HttpServletResponse response,
            HttpSession session,
            Model model
    ) {
        try {
            //  1. Récupérer l'utilisateur connecté
            Long utilisateurId = (Long) session.getAttribute("utilisateurId");
            if (utilisateurId == null) {
                return "redirect:/api/inscri/login";
            }

            List<PanierItem> panierItem = panierService.getPanierUti(utilisateurId);

            //  Calcul du total en Java — pas de stream() Thymeleaf
            long total = panierItem.stream()
                    .mapToLong(item ->
                            (long)(item.getQuantite() * item.getProduit().getPrix()))
                    .sum();
            List<Commande> commandes = commandeRepository.findByIdUser(utilisateurId);
            boolean existe = commandes.stream().anyMatch(c -> c.getId_user().equals(utilisateurId));
            //boolean existe = commandes.stream().anyMatch(c -> Objects.equals(c.getId(), idRecherche));

            if(existe){
                commandeRepository.deleteAll(commandes);
            }
            // ✅ 2. Enregistrer la commande
            Commande commande = new Commande();
            commande.setNom(nom);
            commande.setTel(tel);
            commande.setLieu(lieu);
            commande.setLatitude(latitude);
            commande.setLongitude(longitude);
            commande.setId_user(utilisateurId);

            commande = commandeRepository.save(commande);

            // ✅ 3. Le développeur remplit ici — jamais visible dans le navigateur
            InfoPaiDevHelper info = new InfoPaiDevHelper();
            info.setApiKey("paysim_235247a8d32bddccdbdb397bcaef8c5761680c960806d8df2d63f0ef895ff3f9");
            //info.setApiKey("paysim_511a7a8b192b601535347d24d244d92f09ea50d7e37b5d75722fa0f70acd0b65");
            info.setIdOrder(String.valueOf(commande.getId())); // ← ID de la commande enregistrée
            info.setTotalprice(new BigDecimal(total));
            info.setInfoNumber("0388181197");
            info.setEmail("gym@email.com");

            ValueQr valueQr = paySimService.setup(info, response);
            model.addAttribute("valueKey", valueQr.getValueKey());


            // ✅ Récupérer le jwtApiKey depuis les headers Set-Cookie
            String jwtToken = "";
            System.out.println("=== SET-COOKIE HEADERS ===");
            for (String header : response.getHeaders("Set-Cookie")) {
                if (header.contains("jwtApiKey=")) {
                    jwtToken = header.split("jwtApiKey=")[1].split(";")[0];
                    break;
                }
            }
            System.out.println("jwtToken extrait: [" + jwtToken + "]");
            model.addAttribute("jwtToken", jwtToken);
            return "awaitpay";

        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            return "commandePage";
        }
    }

    // ─── PAGE 3 : Paiement validé ──────────────────────────────────────
    @GetMapping("/success")
    public String page3(
            HttpServletResponse response,
            HttpSession session,
            Model model
    ) {
        Long utilisateurId = (Long) session.getAttribute("utilisateurId");
        if (utilisateurId != null){
            List<PanierItem> produits = panierService.getPanierUti(utilisateurId);
            long total = produits.stream().mapToLong(item ->(long) (item.getQuantite() * item.getProduit().getPrix())).sum();

            List<Commande> commandes = commandeRepository.findByIdUser(utilisateurId);
            Commande commande = commandes.get(0);

            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow();

            // Générer code livraison (6 caractères)
            String codeLivraison = UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();
            emailService.envoyerFacture(
                    utilisateur.getEmail(),
                    utilisateur.getNom() + " " + utilisateur.getPrenom(),
                    commande.getId(),
                    produits,
                    total,
                    codeLivraison
            );

            Livraison livraison = new Livraison();
            livraison.setEmail(utilisateur.getEmail());
            livraison.setNom(utilisateur.getNom() + " " + utilisateur.getPrenom());
            livraison.setTotalPrice((total));
            livraison.setCode_liv(codeLivraison);
            livraison.setOk(false);

            livraisonService.saveLivraison(livraison);

            panierService.viderPanier(utilisateurId);
            commandeRepository.deleteByIdUser(utilisateurId);

        }
        return "succespay";

    }
}
