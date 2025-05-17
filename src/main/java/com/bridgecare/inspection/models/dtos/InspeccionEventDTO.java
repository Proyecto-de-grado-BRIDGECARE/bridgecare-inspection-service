package com.bridgecare.inspection.models.dtos;

import java.io.Serializable;
import java.util.List;

public class InspeccionEventDTO {
    private Long inspeccionId;
    private List<ComponenteDTO> componentes;

    // getters y setters

    public static class ComponenteDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String nombre;
        private Integer calificacion;

        private Integer tipoDanio;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Integer getCalificacion() {
            return calificacion;
        }

        public void setCalificacion(Integer calificacion) {
            this.calificacion = calificacion;
        }

        public Integer getTipoDanio() {
            return tipoDanio;
        }

        public void setTipoDanio(Integer tipoDanio) {
            this.tipoDanio = tipoDanio;
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
