package com.duoc.dsy2206equipments.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.duoc.dsy2206equipments.config.FeignBasicAuthConfig;
import com.duoc.dsy2206equipments.models.VitalSign;

@FeignClient(name = "vitalsign-service", url = "http://172.31.21.28:8082/api/vitalsign", configuration = FeignBasicAuthConfig.class)
public interface VitalSignClient {

    @GetMapping
    ResponseEntity<List<VitalSign>> getAllVitalSigns();
    
    @PostMapping
    ResponseEntity<Object> createVitalSign(@RequestBody VitalSign vitalSign);

    @GetMapping("/{id}")
    ResponseEntity<VitalSign> getVitalSignById(@PathVariable int id);

    @GetMapping("/last/patient/{id}")
    ResponseEntity<VitalSign> getLastVitalSignByPatient(@PathVariable int id);

    @GetMapping("/patient/{id}")
    ResponseEntity<List<VitalSign>> getVitalSignByPatient(@PathVariable int id);

    @PutMapping("/{id}")
    ResponseEntity<VitalSign> updateVitalSign(@PathVariable int id, @RequestBody VitalSign updatedVitalSign );

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteVitalSign(@PathVariable int id);

}
