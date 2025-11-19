package com.teranvet.repository;

import com.teranvet.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Integer> {

    /**
     * CORRECCIÓN: Define la consulta explícitamente con JPQL para evitar que
     * Hibernate intente enlazar este método con el Stored Procedure
     * sp_ValidarUsuario.
     */
    @Query("SELECT u FROM UsuarioSistema u WHERE u.email = :email")
    Optional<UsuarioSistema> findByEmail(@Param("email") String email);

    Optional<UsuarioSistema> findByEmailAndEstado(String email, UsuarioSistema.Estado estado);
}