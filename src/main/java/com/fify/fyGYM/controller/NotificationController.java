package com.fify.fyGYM.controller;

import com.fify.fyGYM.model.Notification;
import com.fify.fyGYM.repository.NotificationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // ===================== NOMBRE DE NOTIFICATIONS NON LUES =====================
    @GetMapping("/count")
    @ResponseBody
    public long getNotificationCount(HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) return 0;
        return notificationRepository.countByDestinataireIdAndLuFalse(userId);
    }

    // ===================== LISTE DES NOTIFICATIONS =====================
    @GetMapping
    @ResponseBody
    public List<Notification> getNotifications(HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        if (userId == null) return new ArrayList<>();

        return notificationRepository.findByDestinataireIdAndLuFalseOrderByDateNotificationDesc(userId);
    }

    // ===================== MARQUER COMME LU + REDIRECTION =====================
    @PostMapping("/{id}/read")
    @ResponseBody
    public Map<String, Object> markAsRead(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("utilisateurId");
        Map<String, Object> response = new HashMap<>();

        Notification notif = notificationRepository.findById(id).orElse(null);

        if (notif != null && notif.getDestinataire().getId().equals(userId)) {
            notif.setLu(true);
            notificationRepository.save(notif);

            response.put("success", true);

            // Redirection vers la publication du commentaire
            if (notif.getCommentaire() != null && notif.getCommentaire().getPublication() != null) {
                response.put("publicationId", notif.getCommentaire().getPublication().getId());
            }
        } else {
            response.put("success", false);
        }

        return response;
    }
}