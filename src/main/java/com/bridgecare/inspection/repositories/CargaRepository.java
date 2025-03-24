package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.Carga;

public interface CargaRepository extends JpaRepository<Carga, Long>{
    
}
