package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Apoyo;

public interface ApoyoRepository extends JpaRepository<Apoyo, Long> {
    
}
