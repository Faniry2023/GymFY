package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Livraison;
import com.fify.fyGYM.repository.LivraisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LivraisonService {
    @Autowired
    private LivraisonRepository livraisonRepository;

    public void saveLivraison (Livraison livraison){
        livraisonRepository.save(livraison);
    }
}
