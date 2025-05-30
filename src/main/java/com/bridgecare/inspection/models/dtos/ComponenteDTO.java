package com.bridgecare.inspection.models.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ComponenteDTO {
    private String nomb;
    private Integer calificacion;
    private String mantenimiento;
    private String inspEesp;
    private Integer numeroFfotos;
    private Integer tipoDanio;
    private String danio;
    private List<ReparacionDTO> reparacion;
    @JsonProperty("imagenUrls")
    private List<String> imagePaths = new ArrayList<>();
    @JsonProperty("componente_uuid")
    private String componenteUuid;

    public String getNomb() {
        return nomb;
    }

    public void setNomb(String nomb) {
        this.nomb = nomb;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(String mantenimiento) {
        this.mantenimiento = mantenimiento;
    }

    public String getInspEesp() {
        return inspEesp;
    }

    public void setInspEesp(String inspEesp) {
        this.inspEesp = inspEesp;
    }

    public Integer getNumeroFfotos() {
        return numeroFfotos;
    }

    public void setNumeroFfotos(Integer numeroFfotos) {
        this.numeroFfotos = numeroFfotos;
    }

    public Integer getTipoDanio() {
        return tipoDanio;
    }

    public void setTipoDanio(Integer tipoDanio) {
        this.tipoDanio = tipoDanio;
    }

    public String getDanio() {
        return danio;
    }

    public void setDanio(String danio) {
        this.danio = danio;
    }

    public List<ReparacionDTO> getReparacion() {
        return reparacion;
    }

    public void setReparacion(List<ReparacionDTO> reparacion) {
        this.reparacion = reparacion;
    }

    public String getComponenteUuid() {
        return componenteUuid;
    }

    public void setComponenteUuid(String componenteUuid) {
        this.componenteUuid = componenteUuid;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
