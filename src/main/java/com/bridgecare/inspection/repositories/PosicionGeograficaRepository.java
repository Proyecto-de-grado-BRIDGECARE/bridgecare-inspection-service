package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.PosicionGeografica;

public interface PosicionGeograficaRepository extends JpaRepository<PosicionGeografica, Long> {
    
}
