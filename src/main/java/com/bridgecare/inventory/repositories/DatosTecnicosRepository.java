package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.DatosTecnicos;

public interface DatosTecnicosRepository extends JpaRepository<DatosTecnicos, Long> {
    
}
