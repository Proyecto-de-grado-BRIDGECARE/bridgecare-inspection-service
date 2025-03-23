package com.bridgecare.inventory.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.inventory.models.entities.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByBridge(Puente puente);
}
