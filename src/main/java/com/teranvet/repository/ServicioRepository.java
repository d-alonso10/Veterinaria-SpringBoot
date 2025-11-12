package com.teranvet.repository;

import com.teranvet.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    
    Optional<Servicio> findByCodigo(String codigo);
    
    List<Servicio> findByCategoria(Servicio.Categoria categoria);
    
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria ORDER BY s.nombre")
    List<Servicio> findByCategoriaSorted(@Param("categoria") Servicio.Categoria categoria);
}
