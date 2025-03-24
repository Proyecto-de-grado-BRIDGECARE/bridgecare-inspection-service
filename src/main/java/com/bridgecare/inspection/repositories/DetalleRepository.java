package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Detalle;

public interface DetalleRepository extends JpaRepository<Detalle, Long> {
    
}
