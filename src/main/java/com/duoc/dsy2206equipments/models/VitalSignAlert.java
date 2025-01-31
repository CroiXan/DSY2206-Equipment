package com.duoc.dsy2206equipments.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VitalSignAlert {

    private int idPaciente;
    private String nombrePaciente;

    private String frecuenciaCardiaca;
    private String gravedadFrecuenciaCardiaca;

    private String frecuenciaRespiratoria;
    private String gravedadFrecuenciaRespiratoria;

    private String presionArterialSistolica;
    private String gravedadPresionArterialSistolicaa;

    private String presionArterialDiastolica;
    private String gravedadPresionArterialDiastolica;

    private String temperaturaCorporal;
    private String gravedadTemperaturaCorporal;

    private String saturacionOxigeno;
    private String gravedadSaturacionOxigeno;

    private LocalDateTime instante;

}
