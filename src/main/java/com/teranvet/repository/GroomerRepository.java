package com.teranvet.repository;

import com.teranvet.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroomerRepository extends JpaRepository<Groomer, Integer> {
    
    List<Groomer> findAll();
    
    List<Groomer> findByNombreContainingIgnoreCase(String nombre);
}
