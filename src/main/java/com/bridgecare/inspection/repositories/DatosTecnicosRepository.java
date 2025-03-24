package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.DatosTecnicos;

public interface DatosTecnicosRepository extends JpaRepository<DatosTecnicos, Long> {
    
}
