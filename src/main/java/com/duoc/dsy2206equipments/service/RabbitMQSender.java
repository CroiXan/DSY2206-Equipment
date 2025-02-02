package com.duoc.dsy2206equipments.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend("Alertas_Exchange","ALERT", message);
        System.out.println("Mensaje enviado: " + message);
    }

}
