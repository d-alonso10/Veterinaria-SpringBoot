package com.teranvet.repository;

import com.teranvet.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Integer> {
    
    @Query("SELECT p FROM Promocion p WHERE p.estado = 'activa'")
    List<Promocion> findPromocionesActivas();
}
