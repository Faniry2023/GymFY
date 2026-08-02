package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Message;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.MessageRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import com.fify.fyGYM.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin/coaching")
public class AdminCoachingController {

    @Autowired private MessageService messageService;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private MessageRepository messageRepository;

    // ── Admin répond à un user ──
    @PostMapping("/message/repondre")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> repondre(
            @RequestParam Long userId,
            @RequestParam String contenu,
            HttpSession session) {
        String role = (String) session.getAttribute("role");
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("succes", false);
            return ResponseEntity.status(403).body(res);
        }
        messageService.envoyer(userId, contenu, "ADMIN");
        res.put("succes", true);
        return ResponseEntity.ok(res);
    }

    // ── Notif admin (messages non lus des users) ──
    @GetMapping("/message/notif")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> notifAdmin(HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        res.put("count", messageService.countNonLusAdmin());
        return ResponseEntity.ok(res);
    }

    // ── Messages d'un user spécifique ──
    @GetMapping("/message/{userId}")
    @ResponseBody
    public ResponseEntity<?> messagesUser(@PathVariable Long userId,
                                          HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();

        messageService.marquerLus(userId, "USER");

        // ✅ Retourner une liste simplifiée pour éviter les problèmes JSON
        List<Message> messages = messageService.getMessages(userId);

        List<Map<String, Object>> result = messages.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id",          m.getId());
            map.put("contenu",     m.getContenu());
            map.put("expediteur",  m.getExpediteur());
            map.put("lu",          m.isLu());
            map.put("dateEnvoi",   m.getDateEnvoi().toString());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // Notif par user spécifique
    @GetMapping("/message/notif/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> notifParUser(
            @PathVariable Long userId, HttpSession session) {

        String role = (String) session.getAttribute("role");
        Map<String, Object> res = new HashMap<>();

        if (!"ADMIN".equals(role)) {
            res.put("count", 0);
            return ResponseEntity.ok(res);
        }

        res.put("count", messageService.countNonLusParUser(userId));
        return ResponseEntity.ok(res);
    }

    // ✅ Nouveau : toutes les notifs par user en une seule requête
    @GetMapping("/message/notif/tous")
    @ResponseBody
    public ResponseEntity<?> notifTousUsers(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();

        // Retourne un Map userId → count
        List<Utilisateur> users = utilisateurRepository.findAll();
        Map<Long, Long> result = new HashMap<>();

        users.forEach(u -> {
            long count = messageRepository
                    .countByUtilisateurAndExpediteurAndLuFalse(u, "USER");
            result.put(u.getId(), count);
        });

        return ResponseEntity.ok(result);
    }
}