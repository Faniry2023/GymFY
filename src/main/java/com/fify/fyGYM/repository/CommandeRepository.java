package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Commande;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    @Query("SELECT c FROM Commande c WHERE c.id_user = :idUser")
    List<Commande> findByIdUser(@Param("idUser") Long idUser);

    @Modifying
    @Transactional
    @Query("DELETE FROM Commande c WHERE c.id_user = :idUser")
    void deleteByIdUser(@Param("idUser") Long idUser);
}
