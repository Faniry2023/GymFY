// CommentaireRepository.java
package com.fify.fyGYM.repository;
import com.fify.fyGYM.model.Commentaire;
import com.fify.fyGYM.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByPublicationOrderByDateCommentaireAsc(Publication p);
}