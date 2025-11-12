package com.teranvet.repository;

import com.teranvet.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    
    List<Mascota> findByCliente_IdCliente(Integer idCliente);
    
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT m FROM Mascota m WHERE m.cliente.idCliente = :idCliente")
    List<Mascota> findByClienteId(@Param("idCliente") Integer idCliente);
    
    @Query("SELECT m FROM Mascota m WHERE m.nombre LIKE %:termino% OR m.raza LIKE %:termino%")
    List<Mascota> buscarPorTermino(@Param("termino") String termino);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Busca mascotas usando el SP
     * Llamada al SP: sp_BuscarMascotas
     */
    @Query(value = "CALL sp_BuscarMascotas(:p_termino)", nativeQuery = true)
    List<Object[]> buscarMascotasSP(@Param("p_termino") String termino);
    
    /**
     * Obtiene mascotas por cliente
     * Llamada al SP: sp_ObtenerMascotasPorCliente
     */
    @Query(value = "CALL sp_ObtenerMascotasPorCliente(:p_id_cliente)", nativeQuery = true)
    List<Object[]> obtenerMascotasPorClienteSP(@Param("p_id_cliente") Integer idCliente);
    
    /**
     * Inserta una nueva mascota
     * Llamada al SP: sp_InsertarMascota
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_InsertarMascota(:p_id_cliente, :p_nombre, :p_especie, :p_raza, " +
            ":p_sexo, :p_fecha_nacimiento, :p_microchip, :p_observaciones)", nativeQuery = true)
    void insertarMascota(
            @Param("p_id_cliente") Integer idCliente,
            @Param("p_nombre") String nombre,
            @Param("p_especie") String especie,
            @Param("p_raza") String raza,
            @Param("p_sexo") String sexo,
            @Param("p_fecha_nacimiento") LocalDate fechaNacimiento,
            @Param("p_microchip") String microchip,
            @Param("p_observaciones") String observaciones);
    
    /**
     * Obtiene el historial completo de una mascota
     * Llamada al SP: sp_HistorialMascota
     */
    @Query(value = "CALL sp_HistorialMascota(:p_id_mascota)", nativeQuery = true)
    List<Object[]> historialMascota(@Param("p_id_mascota") Integer idMascota);
}
