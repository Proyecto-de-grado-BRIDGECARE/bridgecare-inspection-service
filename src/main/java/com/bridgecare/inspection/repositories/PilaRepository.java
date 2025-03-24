package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Pila;

public interface PilaRepository extends JpaRepository<Pila, Long> {
    
}
