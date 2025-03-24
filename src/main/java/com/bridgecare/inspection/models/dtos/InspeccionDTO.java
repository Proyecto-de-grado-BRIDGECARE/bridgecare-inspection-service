package com.bridgecare.inspection.models.dtos;

import com.bridgecare.common.models.dtos.PuenteDTO;
import com.bridgecare.common.models.dtos.UsuarioDTO;
import lombok.Data;

import java.util.List;

@Data
public class InspeccionDTO {
    private Integer tiempo;
    private Integer temperatura;
    private Integer administrador;
    private Integer anioProximaInspeccion;
    private Integer observacionesGenerales;
    private List<ComponenteDTO> componente;
    private UsuarioDTO usuario;
    private PuenteDTO puente;


    public Integer getTiempo() {
        return tiempo;
    }

    public void setTiempo(Integer tiempo) {
        this.tiempo = tiempo;
    }

    public Integer getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Integer temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Integer administrador) {
        this.administrador = administrador;
    }

    public Integer getAnioProximaInspeccion() {
        return anioProximaInspeccion;
    }

    public void setAnioProximaInspeccion(Integer anioProximaInspeccion) {
        this.anioProximaInspeccion = anioProximaInspeccion;
    }

    public Integer getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public void setObservacionesGenerales(Integer observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    public List<ComponenteDTO> getComponente() {
        return componente;
    }

    public void setComponente(List<ComponenteDTO> componente) {
        this.componente = componente;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public PuenteDTO getPuente() {
        return puente;
    }

    public void setPuente(PuenteDTO puente) {
        this.puente = puente;
    }
}
