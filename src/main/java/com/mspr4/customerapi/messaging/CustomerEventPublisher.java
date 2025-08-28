package com.mspr4.customerapi.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspr4.customerapi.model.Customer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CustomerEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishCustomerCreated(Customer customer) {
        sendAsJson("customer.created", customer);
    }

    public void publishCustomerUpdated(Customer customer) {
        sendAsJson("customer.updated", customer);
    }

    public void publishCustomerDeleted(Customer customer) {
        sendAsJson("customer.deleted", customer);
    }

    private void sendAsJson(String routingKey, Customer customer) {
        try {
            String json = objectMapper.writeValueAsString(customer);
            rabbitTemplate.convertAndSend("customer-exchange", routingKey, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur de sérialisation du customer", e);
        }
    }
}
