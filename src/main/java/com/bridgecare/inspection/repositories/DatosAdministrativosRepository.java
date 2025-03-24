package com.bridgecare.inspection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.inspection.models.entities.DatosAdministrativos;

public interface DatosAdministrativosRepository  extends JpaRepository<DatosAdministrativos, Long>{
    
}
