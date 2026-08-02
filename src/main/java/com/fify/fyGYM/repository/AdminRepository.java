package com.fify.fyGYM.repository;

import com.fify.fyGYM.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmailAndMdp(String email,String mdp);
}
