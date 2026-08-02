package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/verification")
    public String afficherPageVerification(HttpSession session) {
        if (session.getAttribute("utilisateurEnAttente") == null) {
            return "redirect:/inscription";
        }
        return "verification";
    }

    @PostMapping("/verification")
    public String verifierCode(@RequestParam String code,
                               HttpSession session,
                               Model model) {

        String codeAttendu = (String) session.getAttribute("codeVerification");
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurEnAttente");

        if (utilisateur == null || codeAttendu == null) {
            return "redirect:/inscription";
        }

        if (code.equals(codeAttendu)) {
            utilisateurRepository.save(utilisateur);
            session.removeAttribute("utilisateurEnAttente");
            session.removeAttribute("codeVerification");
            return "redirect:/api/inscri/login?success=registered";
        } else {
            model.addAttribute("erreur", "Code de vérification incorrect");
            return "verification";
        }
    }
}