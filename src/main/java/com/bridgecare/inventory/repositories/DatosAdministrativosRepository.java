package com.bridgecare.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inventory.models.entities.DatosAdministrativos;

public interface DatosAdministrativosRepository  extends JpaRepository<DatosAdministrativos, Long>{
    
}
