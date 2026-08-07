package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Admin;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.service.InscriptionService;
import com.fify.fyGYM.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/inscri")
public class InscriEtLoginController {
    @Autowired
    private InscriptionService inscriptionService;
    private LoginService loginService;

    public InscriEtLoginController(InscriptionService inscriptionService , LoginService loginService ){
        this.inscriptionService = inscriptionService;
        this.loginService = loginService;
    }

    @GetMapping("/inscription")
    public String getPage(){
        return "inscriptionPage";
    }

    @GetMapping("/login")
    public String getPageLog(){
        return "loginPage";
    }

    @PostMapping("/inscription")
    public String creatUti(@ModelAttribute Utilisateur utilisateur,
                           @RequestParam String comfirmMdp,
                           Model model,
                           HttpSession session) {

        // Vérification mot de passe
        if (!utilisateur.getMdp().equals(comfirmMdp)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas !");
            return "inscriptionPage";
        }

        try {
            utilisateur.setComfirm_mdp(comfirmMdp);
            utilisateur.setRole("USER");           // Important

            //String code = String.valueOf((int)(Math.random() * 900000) + 100000);

            //session.setAttribute("utilisateurEnAttente", utilisateur);
            //session.setAttribute("codeVerification", code);

            inscriptionService.saveUtilisateur(utilisateur);

            return "redirect:/api/inscri/login?success=registered";

            //emailService.envoyerCodeVerification(utilisateur.getEmail(),utilisateur.getPrenom(), code);

            //return "redirect:/verification";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            return "inscriptionPage";
        }
    }

    //verification Login/admin
    @PostMapping("/verifUti")
    public String verifUti(@ModelAttribute Admin admin, Utilisateur utilisateur, Model model ,HttpSession session){
        Optional<Utilisateur> utiTrouver = loginService.verifUti(utilisateur.getEmail() , utilisateur.getMdp());

        if(utiTrouver.isPresent()){
            session.setAttribute("utilisateurId" , utiTrouver.get().getId());
            session.setAttribute("utilisateurNom" , utiTrouver.get().getNom());
            session.setAttribute("role", "USER");
            return "redirect:/api/produit/acceuil";
        }

        Optional<Admin> adminTrouver = loginService.verifAdmin(admin.getEmail() , admin.getMdp());
        if(adminTrouver.isPresent()){
            session.setAttribute("adminId", adminTrouver.get().getId());
            session.setAttribute("role" , "ADMIN");
            return "redirect:/api/produit/afficheAllProduit";
        }


        model.addAttribute("erreur" , "Email ou mot de passe incorrecte!");
        return "loginPage";
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session){
        session.invalidate();
        return "redirect:/api/inscri/login";
    }
}
