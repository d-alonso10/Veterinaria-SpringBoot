package com.teranvet.repository;

import com.teranvet.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    Optional<Cliente> findByDniRuc(String dniRuc);
    
    Optional<Cliente> findByEmail(String email);
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:termino% OR c.apellido LIKE %:termino% OR c.dniRuc LIKE %:termino%")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);
    
    @Query(value = "CALL sp_BuscarClientes(:p_termino)", nativeQuery = true)
    List<Cliente> buscarClientesProcedimiento(@Param("p_termino") String termino);
}
