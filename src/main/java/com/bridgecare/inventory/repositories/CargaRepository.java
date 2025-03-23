package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.Carga;

public interface CargaRepository extends JpaRepository<Carga, Long>{
    
}
