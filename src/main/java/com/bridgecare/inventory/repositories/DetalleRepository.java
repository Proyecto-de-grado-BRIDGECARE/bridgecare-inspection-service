package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Detalle;

public interface DetalleRepository extends JpaRepository<Detalle, Long> {
    
}
