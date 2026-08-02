// CoachingRepository.java
package com.fify.fyGYM.repository;
import com.fify.fyGYM.model.Coaching;
import com.fify.fyGYM.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CoachingRepository extends JpaRepository<Coaching, Long> {
    Optional<Coaching> findByUtilisateur(Utilisateur utilisateur);
}