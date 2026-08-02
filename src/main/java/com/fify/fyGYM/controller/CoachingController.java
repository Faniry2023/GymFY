package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.*;
import com.fify.fyGYM.repository.NotificationRepository;
import com.fify.fyGYM.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.Notification;
import java.util.*;

@Controller
@RequestMapping("/api/coaching")
public class CoachingController {

    @Autowired
    private CoachingService coachingService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private NotificationRepository notificationRepository;

    // ===================== PAGE PRINCIPALE =====================
    @GetMapping
    public String pageCoaching(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return "redirect:/api/inscri/login";
        }

        Optional<Coaching> coachingOpt = coachingService.findByUtilisateur(userId);
        long notifMsg = messageService.countNonLusUser(userId);
        List<Publication> publications = publicationService.getParUser(userId);

        model.addAttribute("coaching", coachingOpt.orElse(null));
        model.addAttribute("publications", publications);
        model.addAttribute("notifMsg", notifMsg);
        model.addAttribute("messages", coachingOpt.isPresent()
                ? messageService.getMessages(userId)
                : List.of());

        return "coachingPage";
    }

    // ── Réaction ──
    @PostMapping("/publication/reagir/{pubId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reagir(
            @PathVariable Long pubId,
            @RequestParam String emoji,
            HttpSession session) {

        Map<String, Object> res = new HashMap<>();
        Long userId = (Long) session.getAttribute("utilisateurId");

        if (userId == null) {
            res.put("succes", false);
            res.put("message", "Non connecté");
            return ResponseEntity.status(401).body(res);
        }

        try {
            Map<String, Object> data =
                    publicationService.reagir(pubId, userId, emoji);
            data.put("succes", true);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            res.put("succes", false);
            res.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // ===================== SAUVEGARDE PROFIL COACHING =====================
    @PostMapping("/sauvegarder")
    public String sauvegarder(@RequestParam double taille,
                              @RequestParam double poids,
                              @RequestParam int age,
                              @RequestParam String genre,
                              @RequestParam(required = false) MultipartFile photo,
                              HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return "redirect:/api/inscri/login";
        }
        coachingService.sauvegarder(userId, taille, poids, age, genre, photo);
        return "redirect:/api/coaching";
    }

    // ===================== PUBLIER PUBLICATION =====================
    @PostMapping("/publication/publier")
    public String publier(@RequestParam(required = false) MultipartFile photoAvant,
                          @RequestParam(required = false) MultipartFile photoApres,
                          @RequestParam(required = false) String legende,
                          HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return "redirect:/api/inscri/login";
        }
        publicationService.publier(userId, photoAvant, photoApres, legende);
        return "redirect:/api/coaching";
    }

    // ===================== SUPPRIMER PUBLICATION =====================
    @PostMapping("/publication/supprimer/{id}")
    public String supprimerPub(@PathVariable Long id) {
        publicationService.supprimer(id);
        return "redirect:/api/coaching";
    }

    // ===================== COMMENTER =====================
    @PostMapping("/publication/commenter/{pubId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> commenter(
            @PathVariable Long pubId,
            @RequestParam String contenu,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        publicationService.commenter(pubId, userId, contenu);

        Map<String, Object> res = new HashMap<>();
        res.put("succes", true);
        return ResponseEntity.ok(res);
    }

    // ===================== PHOTO PROFIL COACHING =====================
    @GetMapping("/photo/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> photoCoaching(@PathVariable Long userId) {
        byte[] photo = coachingService.getPhoto(userId);
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }

    // ===================== ENVOYER MESSAGE =====================
    @PostMapping("/message/envoyer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> envoyerMessage(
            @RequestParam String contenu, HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        Long userId = (Long) session.getAttribute("utilisateurId");

        if (userId == null) {
            res.put("succes", false);
            return ResponseEntity.status(401).body(res);
        }

        messageService.envoyer(userId, contenu, "USER");
        messageService.marquerLus(userId, "ADMIN");

        res.put("succes", true);
        return ResponseEntity.ok(res);
    }

    // ===================== NOTIFICATION MESSAGES =====================
    @GetMapping("/message/notif")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> notifMessage(HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        Long userId = (Long) session.getAttribute("utilisateurId");

        if (userId == null) {
            res.put("count", 0);
            return ResponseEntity.ok(res);
        }

        res.put("count", messageService.countNonLusUser(userId));
        return ResponseEntity.ok(res);
    }

    // ===================== LISTE MESSAGES JSON =====================
    @GetMapping("/message/liste")
    @ResponseBody
    public ResponseEntity<?> getMessagesJson(HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        messageService.marquerLus(userId, "ADMIN");
        List<Message> messages = messageService.getMessages(userId);

        List<Map<String, Object>> result = messages.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("contenu", m.getContenu());
            map.put("expediteur", m.getExpediteur());
            map.put("dateEnvoi", m.getDateEnvoi()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // ===================== IMAGES PUBLICATIONS =====================
    @GetMapping("/publication/photoAvant/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> photoAvant(@PathVariable Long id) {
        byte[] photo = publicationService.getPhotoAvant(id);
        if (photo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
    }

    @GetMapping("/publication/photoApres/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> photoApres(@PathVariable Long id) {
        byte[] photo = publicationService.getPhotoApres(id);
        if (photo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
    }

    // ── Compteurs d'une publication ──
    @GetMapping("/publication/compteurs/{pubId}")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> compteurs(@PathVariable Long pubId) {
        return ResponseEntity.ok(publicationService.getCompteurs(pubId));
    }

    // Nombre de notifications non lues
    @GetMapping("/notifications/count")
    @ResponseBody
    public long getNotificationCount(HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) return 0;
        return notificationRepository.countByDestinataireIdAndLuFalse(userId);
    }

}