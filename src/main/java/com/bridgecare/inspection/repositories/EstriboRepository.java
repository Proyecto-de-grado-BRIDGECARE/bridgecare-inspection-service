package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Estribo;

public interface EstriboRepository extends JpaRepository<Estribo, Long> {
    
}
