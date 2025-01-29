package com.duoc.dsy2206equipments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class Dsy2206equipmentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dsy2206equipmentsApplication.class, args);
	}

}
