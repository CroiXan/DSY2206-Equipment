package com.duoc.dsy2206equipments.task;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.duoc.dsy2206equipments.client.PatientClient;
import com.duoc.dsy2206equipments.client.VitalSignClient;
import com.duoc.dsy2206equipments.models.Patient;
import com.duoc.dsy2206equipments.models.VitalSign;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EquipmentTask {

    private final PatientClient patientClient;
    private final VitalSignClient vitalSignClient;
    private final ObjectMapper objectMapper;

    public EquipmentTask(PatientClient patientClient, VitalSignClient vitalSignClient, ObjectMapper objectMapper) {
        this.patientClient = patientClient;
        this.vitalSignClient = vitalSignClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 60000)
    public void generatePatientsVitalSigns() {
        ResponseEntity<Object> response = this.patientClient.getAllPatients();

        if (!(response.getBody() instanceof String)) {
            try {
                List<Patient> patients = objectMapper.convertValue(
                        response, new TypeReference<List<Patient>>() {
                        });

                for (Patient patient : patients) {
                    int frecuenciaCardiaca = (int) (Math.random() * 301);
                    int frecuenciaRespiratoria = (int) (Math.random() * 101);
                    int presionArterialSistolica = (int) (Math.random() * 301);
                    int presionArterialDiastolica = (int) (Math.random() * 201);
                    Double temperaturaCorporal = Math.random() * 100;
                    Double saturacionOxigeno = Math.random() * 100;

                    VitalSign newVitalSign = new VitalSign();
                    newVitalSign.setIdPaciente(patient.getId());
                    newVitalSign.setFrecuenciaCardiaca(frecuenciaCardiaca);
                    newVitalSign.setFrecuenciaRespiratoria(frecuenciaRespiratoria);
                    newVitalSign.setPresionArterialDiastolica(presionArterialDiastolica);
                    newVitalSign.setPresionArterialSistolica(presionArterialSistolica);
                    newVitalSign.setTemperaturaCorporal(temperaturaCorporal);
                    newVitalSign.setSaturacionOxigeno(saturacionOxigeno);

                    this.vitalSignClient.createVitalSign(newVitalSign);

                    this.checkPatientAlert(newVitalSign);
                }

            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta a lista", e);
            }
        }
    }

    private void checkPatientAlert(VitalSign newVitalSign) {
        StringBuilder alertMessage = new StringBuilder();

        if (newVitalSign.getFrecuenciaCardiaca() > 40 && newVitalSign.getFrecuenciaCardiaca() <= 59) {
            alertMessage.append("Frecuencia Cardiaca : ")
                    .append(newVitalSign.getFrecuenciaCardiaca() + " lpm | Bradicardia moderada\n");
        } else if (newVitalSign.getFrecuenciaCardiaca() >= 101 && newVitalSign.getFrecuenciaCardiaca() < 120) {
            alertMessage.append("Frecuencia Cardiaca : ")
                    .append(newVitalSign.getFrecuenciaCardiaca() + " lpm | Taquicardia leve\n");
        } else if (newVitalSign.getFrecuenciaCardiaca() <= 40) {
            alertMessage.append("Frecuencia Cardiaca : ")
                    .append(newVitalSign.getFrecuenciaCardiaca() + " lpm | Bradicardia severa\n");
        } else if (newVitalSign.getFrecuenciaCardiaca() >= 120) {
            alertMessage.append("Frecuencia Cardiaca : ")
                    .append(newVitalSign.getFrecuenciaCardiaca() + " lpm | Taquicardia sinusal o arritmia severa\n");
        }

        if (newVitalSign.getFrecuenciaRespiratoria() > 5 && newVitalSign.getFrecuenciaRespiratoria() <= 11) {
            alertMessage.append("Frecuencia Respiratoria : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " rpm | Hipoventilación\n");
        } else if (newVitalSign.getFrecuenciaRespiratoria() >= 21 && newVitalSign.getFrecuenciaRespiratoria() < 30) {
            alertMessage.append("Frecuencia Respiratoria : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " rpm | Taquipnea moderada\n");
        } else if (newVitalSign.getFrecuenciaRespiratoria() <= 5) {
            alertMessage.append("Frecuencia Respiratoria : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " rpm | Insuficiencia respiratoria grave\n");
        } else if (newVitalSign.getFrecuenciaRespiratoria() >= 30) {
            alertMessage.append("Frecuencia Respiratoria : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " rpm | Hiperventilación grave\n");
        }

        if (newVitalSign.getPresionArterialSistolica() > 80 && newVitalSign.getPresionArterialSistolica() <= 89) {
            alertMessage.append("Presion Arterial Sistolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | Hipotensión leve \n");
        } else if (newVitalSign.getPresionArterialSistolica() >= 121 && newVitalSign.getPresionArterialSistolica() < 139) {
            alertMessage.append("Presion Arterial Sistolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | prehipertensión \n");
        } else if (newVitalSign.getPresionArterialSistolica() <= 80) {
            alertMessage.append("Presion Arterial Sistolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | Hipotensión severa \n");
        } else if (newVitalSign.getPresionArterialSistolica() >= 140) {
            alertMessage.append("Presion Arterial Sistolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | Hipotensión grave \n");
        }

        if (newVitalSign.getPresionArterialDiastolica() > 50 && newVitalSign.getPresionArterialDiastolica() <= 59) {
            alertMessage.append("Presion Arterial Diastolica : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " mmHg | Hipotensión leve\n");
        } else if (newVitalSign.getPresionArterialDiastolica() >= 81 && newVitalSign.getPresionArterialDiastolica() < 89) {
            alertMessage.append("Presion Arterial Diastolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | prehipertensión \n");
        } else if (newVitalSign.getPresionArterialDiastolica() <= 50) {
            alertMessage.append("Presion Arterial Diastolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | Hipotensión severa \n");
        } else if (newVitalSign.getPresionArterialDiastolica() >= 90) {
            alertMessage.append("Presion Arterial Diastolica : ")
                    .append(newVitalSign.getPresionArterialSistolica() + " mmHg | Hipotensión grave \n");
        }

        if (newVitalSign.getTemperaturaCorporal() > 35 && newVitalSign.getTemperaturaCorporal() <= 36) {
            alertMessage.append("Temperatura Corporal : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " °C | Hipotermia leve\n");
        } else if (newVitalSign.getTemperaturaCorporal() >= 37.6 && newVitalSign.getTemperaturaCorporal() < 38.5) {
            alertMessage.append("Temperatura Corporal : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " °C | Fiebre moderada\n");
        } else if (newVitalSign.getTemperaturaCorporal() <= 35) {
            alertMessage.append("Temperatura Corporal : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " °C | Hipotermia severa\n");
        } else if (newVitalSign.getTemperaturaCorporal() >= 38.5) {
            alertMessage.append("Temperatura Corporal : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + " °C | Fiebre alta\n");
        }

        if (newVitalSign.getSaturacionOxigeno() > 90 && newVitalSign.getSaturacionOxigeno() <= 94) {
            alertMessage.append("Saturacion Oxigeno : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + "% | Hipoxemia leve\n");
        } else if (newVitalSign.getSaturacionOxigeno() <= 90) {
            alertMessage.append("Saturacion Oxigeno : ")
                    .append(newVitalSign.getFrecuenciaRespiratoria() + "% | Hipoxia severa\n");
        }

    }
}
