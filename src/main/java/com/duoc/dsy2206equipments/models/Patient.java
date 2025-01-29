package com.duoc.dsy2206equipments.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Patient {
    private int id;

    private String rut;

    private String password;

    private String nombre;

    private String apellidos;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate bornDate; 
}
