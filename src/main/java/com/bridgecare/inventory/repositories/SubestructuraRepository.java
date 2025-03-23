package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Subestructura;

public interface SubestructuraRepository extends JpaRepository<Subestructura, Long> {
    
}
