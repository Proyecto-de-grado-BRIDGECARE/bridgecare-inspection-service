package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.MiembrosInteresados;

public interface MiembrosInteresadosRepository extends JpaRepository<MiembrosInteresados, Long> {
    
}
