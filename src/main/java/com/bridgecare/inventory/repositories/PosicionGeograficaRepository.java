package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.PosicionGeografica;

public interface PosicionGeograficaRepository extends JpaRepository<PosicionGeografica, Long> {
    
}
