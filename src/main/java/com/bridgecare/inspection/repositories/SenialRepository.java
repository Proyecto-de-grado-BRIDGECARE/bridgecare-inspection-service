package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Senial;

public interface SenialRepository extends JpaRepository<Senial, Long> {
    
}
