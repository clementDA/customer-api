package com.mspr4.customerapi.service;

import com.mspr4.customerapi.model.Customer;
import com.mspr4.customerapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer getCustomerById(UUID id) {
        return repository.findById(id).orElse(null);
    }


    public Customer saveCustomer(Customer customer) {
        return repository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        repository.deleteById(id);
    }
}
