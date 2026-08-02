// CoachingService.java
package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Coaching;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.CoachingRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
public class CoachingService {

    @Autowired private CoachingRepository coachingRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;

    // Vérifie si l'user a déjà rempli le formulaire coaching
    public Optional<Coaching> findByUtilisateur(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return coachingRepository.findByUtilisateur(u);
    }

    // Sauvegarde le profil coaching
    public void sauvegarder(Long userId, double taille, double poids,
                            int age, String genre, MultipartFile photo) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));

        Coaching coaching = coachingRepository.findByUtilisateur(u)
                .orElse(new Coaching());

        coaching.setUtilisateur(u);
        coaching.setTaille(taille);
        coaching.setPoids(poids);
        coaching.setAge(age);
        coaching.setGenre(genre);

        if (photo != null && !photo.isEmpty()) {
            try {
                coaching.setPhoto(photo.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erreur photo");
            }
        }
        coachingRepository.save(coaching);
    }

    // Endpoint image photo coaching
    public byte[] getPhoto(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
        return coachingRepository.findByUtilisateur(u)
                .map(Coaching::getPhoto)
                .orElse(null);
    }
}