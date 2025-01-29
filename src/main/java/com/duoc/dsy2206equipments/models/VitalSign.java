package com.duoc.dsy2206equipments.models;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VitalSign {

    private int id;

    private int idPaciente;

    private int frecuenciaCardiaca;

    private int frecuenciaRespiratoria;

    private int presionArterialSistolica;

    private int presionArterialDiastolica;

    private Double temperaturaCorporal;

    private Double saturacionOxigeno;

    private LocalDateTime instante;

}
