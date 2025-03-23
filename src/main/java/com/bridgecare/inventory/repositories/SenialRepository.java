package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Senial;

public interface SenialRepository extends JpaRepository<Senial, Long> {
    
}
