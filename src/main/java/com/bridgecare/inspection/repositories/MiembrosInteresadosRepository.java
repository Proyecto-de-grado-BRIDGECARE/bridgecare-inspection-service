package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.MiembrosInteresados;

public interface MiembrosInteresadosRepository extends JpaRepository<MiembrosInteresados, Long> {
    
}
