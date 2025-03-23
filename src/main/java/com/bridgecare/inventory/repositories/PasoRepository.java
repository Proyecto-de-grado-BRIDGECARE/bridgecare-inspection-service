package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Paso;

public interface PasoRepository extends JpaRepository<Paso, Long> {
    
}
