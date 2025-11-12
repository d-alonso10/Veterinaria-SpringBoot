package com.teranvet.repository;

import com.teranvet.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    
    List<Mascota> findByCliente_IdCliente(Integer idCliente);
    
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT m FROM Mascota m WHERE m.cliente.idCliente = :idCliente")
    List<Mascota> findByClienteId(@Param("idCliente") Integer idCliente);
    
    @Query("SELECT m FROM Mascota m WHERE m.nombre LIKE %:termino% OR m.raza LIKE %:termino%")
    List<Mascota> buscarPorTermino(@Param("termino") String termino);
}
