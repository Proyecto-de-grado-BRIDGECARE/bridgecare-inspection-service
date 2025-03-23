package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Pila;

public interface PilaRepository extends JpaRepository<Pila, Long> {
    
}
