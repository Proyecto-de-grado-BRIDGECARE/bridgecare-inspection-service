package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Estribo;

public interface EstriboRepository extends JpaRepository<Estribo, Long> {
    
}
