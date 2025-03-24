package com.bridgecare.inspection.models.entities;

import jakarta.persistence.*;

import java.time.Year;

@Entity
@Table(name = "reparacion")
public class Reparacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name= "tipo")
    private String tipo;

    @Column(name= "cantidad")
    private Integer cantidad;

    @Column(name= "anio")
    private Year anio;

    @Column(name= "costo")
    private Integer costo;

    @ManyToOne
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Year getAnio() {
        return anio;
    }

    public void setAnio(Year anio) {
        this.anio = anio;
    }

    public Integer getCosto() {
        return costo;
    }

    public void setCosto(Integer costo) {
        this.costo = costo;
    }

    public Componente getComponente() {
        return componente;
    }

    public void setComponente(Componente componente) {
        this.componente = componente;
    }
}
