package com.teranvet.repository;

import com.teranvet.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Integer> {
    
    Optional<UsuarioSistema> findByEmail(String email);
    
    Optional<UsuarioSistema> findByEmailAndActivoTrue(String email);
}
