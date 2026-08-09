package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.PanierItem;
import com.fify.fyGYM.service.PanierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/commande")
public class CommandeController {

    @Autowired
    private PanierService panierService;

    @GetMapping("/confirmation")
    public String pageConfirmation(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return "redirect:/api/inscri/login";
        }

        List<PanierItem> panierItem = panierService.getPanierNonPayer(userId);

        // ✅ Calcul du total en Java — pas de stream() Thymeleaf
        long total = panierItem.stream()
                .mapToLong(item ->
                        (long)(item.getQuantite() * item.getProduit().getPrix()))
                .sum();

        model.addAttribute("panierItem", panierItem);
        model.addAttribute("total", total);

        return "commandePage";
    }


    @GetMapping("/view")
    public  String viewCommande(){
        int id_user = 1;

        return  "";
    }
}