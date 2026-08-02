package com.fify.fyGYM.service;

import com.fify.fyGYM.model.Admin;
import com.fify.fyGYM.model.Utilisateur;
import com.fify.fyGYM.repository.AdminRepository;
import com.fify.fyGYM.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;
    private AdminRepository adminRepository;

    public LoginService(LoginRepository loginRepository , AdminRepository adminRepository){
        this.loginRepository = loginRepository;
        this.adminRepository = adminRepository;
    }

    public Optional<Utilisateur> verifUti(String email , String mdp){
        return loginRepository.findByEmailAndMdp(email, mdp);
    }
    public Optional<Admin> verifAdmin(String email , String mdp){
        return adminRepository.findByEmailAndMdp(email , mdp);
    }

}
