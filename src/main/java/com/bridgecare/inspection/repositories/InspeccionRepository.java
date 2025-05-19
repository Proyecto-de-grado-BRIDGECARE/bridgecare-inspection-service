package com.bridgecare.inspection.repositories;

import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.inspection.models.entities.Inspeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InspeccionRepository extends JpaRepository<Inspeccion,Long> {
    List<Inspeccion> findByPuente(Puente puente);

    @Query("SELECT i.id FROM Inspeccion i WHERE i.puente.id = :puenteId")
    List<Long> findIdsByPuenteId(@Param("puenteId") Long puenteId);

}
