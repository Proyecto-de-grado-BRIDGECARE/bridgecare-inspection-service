package com.bridgecare.inspection.models.dtos;

import java.util.List;

public class InspeccionEventDTO {
    private Long inspeccionId;
    private List<ComponenteDTO> componentes;

    // getters y setters

    public static class ComponenteDTO {
        private String nombre;
        private Double calificacion;

        // getters y setters


        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Double getCalificacion() {
            return calificacion;
        }

        public void setCalificacion(Double calificacion) {
            this.calificacion = calificacion;
        }
    }

    public Long getInspeccionId() {
        return inspeccionId;
    }

    public void setInspeccionId(Long inspeccionId) {
        this.inspeccionId = inspeccionId;
    }

    public List<ComponenteDTO> getComponentes() {
        return componentes;
    }

    public void setComponentes(List<ComponenteDTO> componentes) {
        this.componentes = componentes;
    }
}
