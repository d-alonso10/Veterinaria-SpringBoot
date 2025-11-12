package com.teranvet.repository;

import com.teranvet.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    Optional<Cliente> findByDniRuc(String dniRuc);
    
    Optional<Cliente> findByEmail(String email);
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:termino% OR c.apellido LIKE %:termino% OR c.dniRuc LIKE %:termino%")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Busca clientes usando el SP
     * Llamada al SP: sp_BuscarClientes
     */
    @Query(value = "CALL sp_BuscarClientes(:p_termino)", nativeQuery = true)
    List<Object[]> buscarClientesSP(@Param("p_termino") String termino);
    
    /**
     * Inserta un nuevo cliente
     * Llamada al SP: sp_InsertarCliente
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_InsertarCliente(:p_nombre, :p_apellido, :p_dni_ruc, :p_email, " +
            ":p_telefono, :p_direccion, :p_preferencias)", nativeQuery = true)
    void insertarCliente(
            @Param("p_nombre") String nombre,
            @Param("p_apellido") String apellido,
            @Param("p_dni_ruc") String dniRuc,
            @Param("p_email") String email,
            @Param("p_telefono") String telefono,
            @Param("p_direccion") String direccion,
            @Param("p_preferencias") String preferencias);
}
