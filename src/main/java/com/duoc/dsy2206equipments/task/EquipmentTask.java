package com.duoc.dsy2206equipments.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.duoc.dsy2206equipments.client.PatientClient;
import com.duoc.dsy2206equipments.client.VitalSignClient;
import com.duoc.dsy2206equipments.models.Patient;
import com.duoc.dsy2206equipments.models.VitalSign;
import com.duoc.dsy2206equipments.models.VitalSignAlert;
import com.duoc.dsy2206equipments.service.RabbitMQSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class EquipmentTask {

    private final PatientClient patientClient;
    private final VitalSignClient vitalSignClient;
    private final ObjectMapper objectMapper;
    private final RabbitMQSender rabbit;

    public EquipmentTask(PatientClient patientClient, VitalSignClient vitalSignClient, ObjectMapper objectMapper,
            RabbitMQSender rabbit) {
        this.patientClient = patientClient;
        this.vitalSignClient = vitalSignClient;
        this.objectMapper = objectMapper;
        this.rabbit = rabbit;
    }

    @Scheduled(fixedRate = 60000)
    public void generatePatientsVitalSigns() {

        try {
            ResponseEntity<List<Patient>> response = this.patientClient.getAllPatients();

            for (Patient patient : response.getBody()) {
                int frecuenciaCardiaca = (int) (Math.random() * 301);
                int frecuenciaRespiratoria = (int) (Math.random() * 101);
                int presionArterialSistolica = (int) (Math.random() * 301);
                int presionArterialDiastolica = (int) (Math.random() * 201);
                Double temperaturaCorporal = Math.random() * 50;
                Double saturacionOxigeno = Math.random() * 100;

                VitalSign newVitalSign = new VitalSign();
                newVitalSign.setIdPaciente(patient.getId());
                newVitalSign.setFrecuenciaCardiaca(frecuenciaCardiaca);
                newVitalSign.setFrecuenciaRespiratoria(frecuenciaRespiratoria);
                newVitalSign.setPresionArterialDiastolica(presionArterialDiastolica);
                newVitalSign.setPresionArterialSistolica(presionArterialSistolica);
                newVitalSign.setTemperaturaCorporal(temperaturaCorporal);
                newVitalSign.setSaturacionOxigeno(saturacionOxigeno);
                newVitalSign.setInstante(LocalDateTime.now());

                this.vitalSignClient.createVitalSign(newVitalSign);

                this.checkPatientAlert(newVitalSign,patient.getNombre() + " " + patient.getApellidos());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al convertir la respuesta a lista", e);
        }
    }

    private void checkPatientAlert(VitalSign newVitalSign, String nombrePaciente) {
        VitalSignAlert newAlert = new VitalSignAlert();
        boolean sendAlert = false;

        newAlert.setIdPaciente(newVitalSign.getIdPaciente());
        newAlert.setNombrePaciente(nombrePaciente);
        newAlert.setInstante(newVitalSign.getInstante());
        newAlert.setFrecuenciaCardiaca(newVitalSign.getFrecuenciaCardiaca() + " lpm");
        newAlert.setGravedadFrecuenciaCardiaca("Normal");
        newAlert.setFrecuenciaRespiratoria(newVitalSign.getFrecuenciaRespiratoria() + " rpm");
        newAlert.setGravedadFrecuenciaRespiratoria("Normal");
        newAlert.setPresionArterialDiastolica(newVitalSign.getPresionArterialDiastolica() + " mmHg");
        newAlert.setGravedadPresionArterialDiastolica("Normal");
        newAlert.setPresionArterialSistolica(newVitalSign.getPresionArterialSistolica() + " mmHg");
        newAlert.setGravedadPresionArterialSistolicaa("Normal");
        newAlert.setTemperaturaCorporal(newVitalSign.getTemperaturaCorporal() + " °C");
        newAlert.setGravedadTemperaturaCorporal("Normal");
        newAlert.setSaturacionOxigeno(newVitalSign.getSaturacionOxigeno() + " %");
        newAlert.setGravedadSaturacionOxigeno("Normal");

        if (newVitalSign.getFrecuenciaCardiaca() > 40 && newVitalSign.getFrecuenciaCardiaca() <= 59) {
            newAlert.setGravedadFrecuenciaCardiaca("Bradicardia moderada");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaCardiaca() >= 101 && newVitalSign.getFrecuenciaCardiaca() < 120) {
            newAlert.setGravedadFrecuenciaCardiaca("Taquicardia leve");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaCardiaca() <= 40) {
            newAlert.setGravedadFrecuenciaCardiaca("Bradicardia severa");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaCardiaca() >= 120) {
            newAlert.setGravedadFrecuenciaCardiaca("Taquicardia sinusal o arritmia severa");
            sendAlert = true;
        }

        if (newVitalSign.getFrecuenciaRespiratoria() > 5 && newVitalSign.getFrecuenciaRespiratoria() <= 11) {
            newAlert.setGravedadFrecuenciaRespiratoria("Hipoventilación");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaRespiratoria() >= 21 && newVitalSign.getFrecuenciaRespiratoria() < 30) {
            newAlert.setGravedadFrecuenciaRespiratoria("Taquipnea moderada");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaRespiratoria() <= 5) {
            newAlert.setGravedadFrecuenciaRespiratoria("Insuficiencia respiratoria grave");
            sendAlert = true;
        } else if (newVitalSign.getFrecuenciaRespiratoria() >= 30) {
            newAlert.setGravedadFrecuenciaRespiratoria("Hiperventilación grave");
            sendAlert = true;
        }

        if (newVitalSign.getPresionArterialSistolica() > 80 && newVitalSign.getPresionArterialSistolica() <= 89) {
            newAlert.setGravedadPresionArterialSistolicaa("Hipotensión leve");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialSistolica() >= 121
                && newVitalSign.getPresionArterialSistolica() < 139) {
            newAlert.setGravedadPresionArterialSistolicaa("Prehipertensión");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialSistolica() <= 80) {
            newAlert.setGravedadPresionArterialSistolicaa("Hipotensión severa");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialSistolica() >= 140) {
            newAlert.setGravedadPresionArterialSistolicaa("Hipotensión grave");
            sendAlert = true;
        }

        if (newVitalSign.getPresionArterialDiastolica() > 50 && newVitalSign.getPresionArterialDiastolica() <= 59) {
            newAlert.setGravedadPresionArterialDiastolica("Hipotensión leve");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialDiastolica() >= 81
                && newVitalSign.getPresionArterialDiastolica() < 89) {
            newAlert.setGravedadPresionArterialDiastolica("Prehipertensión");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialDiastolica() <= 50) {
            newAlert.setGravedadPresionArterialDiastolica("Hipotensión severa");
            sendAlert = true;
        } else if (newVitalSign.getPresionArterialDiastolica() >= 90) {
            newAlert.setGravedadPresionArterialDiastolica("Hipotensión grave");
            sendAlert = true;
        }

        if (newVitalSign.getTemperaturaCorporal() > 35 && newVitalSign.getTemperaturaCorporal() <= 36) {
            newAlert.setGravedadTemperaturaCorporal("Hipotermia leve");
            sendAlert = true;
        } else if (newVitalSign.getTemperaturaCorporal() >= 37.6 && newVitalSign.getTemperaturaCorporal() < 38.5) {
            newAlert.setGravedadTemperaturaCorporal("Fiebre moderada");
            sendAlert = true;
        } else if (newVitalSign.getTemperaturaCorporal() <= 35) {
            newAlert.setGravedadTemperaturaCorporal("Hipotermia severa");
            sendAlert = true;
        } else if (newVitalSign.getTemperaturaCorporal() >= 38.5) {
            newAlert.setGravedadTemperaturaCorporal("Fiebre alta");
            sendAlert = true;
        }

        if (newVitalSign.getSaturacionOxigeno() > 90 && newVitalSign.getSaturacionOxigeno() <= 94) {
            newAlert.setGravedadSaturacionOxigeno("Hipoxemia leve");
            sendAlert = true;
        } else if (newVitalSign.getSaturacionOxigeno() <= 90) {
            newAlert.setGravedadSaturacionOxigeno("Hipoxia severa");
            sendAlert = true;
        }

        if (sendAlert) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try {
                rabbit.sendMessage(objectMapper.writeValueAsString(newAlert));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        
    }
}
