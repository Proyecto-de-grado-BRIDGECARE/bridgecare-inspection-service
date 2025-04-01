package com.bridgecare.inspection.models.entities;

import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="inspeccion")
public class Inspeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="tiempo")
    private Integer tiempo;

    @Column(name="temperatura")
    private Integer temperatura;

    @Column(name="administrador")
    private Integer administrador;

    @Column(name="anio_proxima_inspeccion")
    private Integer anioProximaInspeccion;

    @Column(name="observaciones_generales")
    private Integer observacionesGenerales;

    @OneToMany(mappedBy = "inspeccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Componente> componentes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "puente_id", nullable = false, unique = true)
    private Puente puente;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<Componente> getComponentes() {
        return componentes;
    }

    public void setComponentes(List<Componente> componentes) {
        this.componentes = componentes;
    }

    public Puente getPuente() {
        return puente;
    }

    public void setPuente(Puente puente) {
        this.puente = puente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
