package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.InscriptionRepository;
import com.fify.fyGYM.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscriptionService {
    @Autowired
    private InscriptionRepository inscriptionRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private InscriptionService(InscriptionRepository inscriptionRepository){
        this.inscriptionRepository = inscriptionRepository;
    }

    public void saveUtilisateur(Utilisateur u) {
        if (u.getComfirm_mdp() == null || u.getComfirm_mdp().trim().isEmpty()) {
            u.setComfirm_mdp(u.getMdp());
        }
        utilisateurRepository.save(u);
    }


}
