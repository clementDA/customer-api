package com.mspr4.customerapi.controller;

import com.mspr4.customerapi.model.Customer;
import com.mspr4.customerapi.service.CustomerService;
import com.mspr4.customerapi.messaging.CustomerEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;
    private final CustomerEventPublisher eventPublisher;

    public CustomerController(CustomerService service, CustomerEventPublisher eventPublisher) {
        this.service = service;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        List<Customer> customers = service.getAllCustomers();

        return customers;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        Customer customer = service.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        Customer saved = service.saveCustomer(customer);
        eventPublisher.publishCustomerCreated(saved);
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID id, @RequestBody Customer updatedCustomer) {
        Customer existing = service.getCustomerById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setFirstName(updatedCustomer.getFirstName());
        existing.setLastName(updatedCustomer.getLastName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setIsProspect(updatedCustomer.getIsProspect());

        Customer saved = service.saveCustomer(existing);
        eventPublisher.publishCustomerUpdated(saved);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        Customer existing = service.getCustomerById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        service.deleteCustomer(id);
        eventPublisher.publishCustomerDeleted(existing); // événement supprimé
        return ResponseEntity.noContent().build();
    }
}
