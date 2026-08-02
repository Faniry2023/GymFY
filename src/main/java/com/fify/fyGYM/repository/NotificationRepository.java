package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Liste des notifications non lues
    List<Notification> findByDestinataireIdAndLuFalse(Long destinataireId);

    // Liste des notifications non lues triées par date (la plus récente en premier)
    List<Notification> findByDestinataireIdAndLuFalseOrderByDateNotificationDesc(Long destinataireId);

    // Compteur des notifications non lues
    long countByDestinataireIdAndLuFalse(Long destinataireId);
}