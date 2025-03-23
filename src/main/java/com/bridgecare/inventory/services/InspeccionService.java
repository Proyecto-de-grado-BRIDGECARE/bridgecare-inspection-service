package com.bridgecare.inspection.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;
import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.models.entities.DatosAdministrativos;
import com.bridgecare.inspection.models.entities.DatosTecnicos;
import com.bridgecare.inspection.models.entities.Estribo;
import com.bridgecare.inspection.models.entities.Inspeccion;
import com.bridgecare.inspection.models.entities.Pila;
import com.bridgecare.inspection.models.entities.Subestructura;
import com.bridgecare.inspection.repositories.ApoyoRepository;
import com.bridgecare.inspection.repositories.CargaRepository;
import com.bridgecare.inspection.repositories.DatosAdministrativosRepository;
import com.bridgecare.inspection.repositories.DatosTecnicosRepository;
import com.bridgecare.inspection.repositories.DetalleRepository;
import com.bridgecare.inspection.repositories.EstriboRepository;
import com.bridgecare.inspection.repositories.InspeccionRepository;
import com.bridgecare.inspection.repositories.MiembrosInteresadosRepository;
import com.bridgecare.inspection.repositories.PasoRepository;
import com.bridgecare.inspection.repositories.PilaRepository;
import com.bridgecare.inspection.repositories.PosicionGeograficaRepository;
import com.bridgecare.inspection.repositories.SenialRepository;
import com.bridgecare.inspection.repositories.SubestructuraRepository;
import com.bridgecare.inspection.repositories.SuperestructuraRepository;

import jakarta.transaction.Transactional;

@SuppressWarnings("unused")
@Service
public class InspeccionService {
    
    @Autowired
    private RestTemplate restTemplate; // HTTP calls

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private DatosTecnicosRepository datosTecnicosRepository;

    @Autowired
    private DatosAdministrativosRepository datosAdministrativosRepository;

    @Autowired
    private SubestructuraRepository subestructuraRepository;

    @Autowired
    private PilaRepository pilaRepository;

    @Autowired
    private EstriboRepository estriboRepository;

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private SenialRepository senialRepository;

    @Autowired
    private ApoyoRepository apoyoRepository;

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private MiembrosInteresadosRepository miembrosInteresadosRepository;

    @Autowired
    private PasoRepository pasoRepository;

    @Autowired
    private PosicionGeograficaRepository posicionGeograficaRepository;

    @Autowired
    private SuperestructuraRepository superestructuraRepository;

    @Transactional
    public Long saveInspeccion(InspeccionDTO request) {
        // Save puente via puente-service (TBD)
        String puenteUrl = "http://puente-service/api/puentes";
        ResponseEntity<Long> response = restTemplate.postForEntity(puenteUrl, request.getPuente(), Long.class);
        Long puenteId = response.getBody();

        Puente puente = new Puente();
        puente.setId(puenteId);
        puente.setNombre(request.getPuente().getNombre());

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setPuente(puente);
        inspeccion.setObservaciones(request.getObservaciones());

        Usuario usuario = new Usuario();
        usuario.setId(request.getUsuario().getId());
        inspeccion.setUsuario(usuario);


        // Save subtables
        if (request.getDatosTecnicos() != null) {
            DatosTecnicos dt = new DatosTecnicos();
            dt.setInspeccion(inspeccion);
            dt.setNumeroLuces(request.getDatosTecnicos().getNumeroLuces());
            // TBD
            datosTecnicosRepository.save(dt);
            inspeccion.setDatosTecnicos(dt);
        }

        if (request.getDatosAdministrativos() != null) {
            DatosAdministrativos da = new DatosAdministrativos();
            da.setInspeccion(inspeccion);
            da.setAnioConstruccion(request.getDatosAdministrativos().getAnioConstruccion());
            // TBD
            datosAdministrativosRepository.save(da);
            inspeccion.setDatosAdministrativos(da);
        }

        // Save subestructura and its subtables
        if (request.getSubestructura() != null) {
            Subestructura sub = new Subestructura();
            sub.setInspeccion(inspeccion);
            sub = subestructuraRepository.save(sub);

            if (request.getSubestructura().getPila() != null) {
                Pila pila = new Pila();
                pila.setSubestructura(sub);
                pila.setTipo(request.getSubestructura().getPila().getTipo());
                // TBD
                pilaRepository.save(pila);
                sub.setPila(pila);
            }

            if (request.getSubestructura().getEstribo() != null) {
                Estribo estribo = new Estribo();
                estribo.setSubestructura(sub);
                estribo.setTipo(request.getSubestructura().getEstribo().getTipo());
                // TBD
                estriboRepository.save(estribo);
                sub.setEstribo(estribo);
            }

            // TBD
            inspeccion.setSubestructura(sub);
        }

        // TBD

        inspeccionRepository.save(inspeccion);
        return inspeccion.getId();
    }
}