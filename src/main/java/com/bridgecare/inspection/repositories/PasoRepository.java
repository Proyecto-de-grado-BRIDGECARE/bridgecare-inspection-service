package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Paso;

public interface PasoRepository extends JpaRepository<Paso, Long> {
    
}
