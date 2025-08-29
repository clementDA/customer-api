package com.mspr4.customerapi.service;

import com.mspr4.customerapi.model.Customer;
import com.mspr4.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCompanyName("Read");
    }

    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(Arrays.asList(customer));

        List<Customer> customers = service.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("John", customers.get(0).getFirstName());
    }

    @Test
    void testGetCustomerById_Found() {
        UUID id = customer.getCustomerId();
        when(repository.findById(id)).thenReturn(java.util.Optional.of(customer));

        Customer found = service.getCustomerById(id);

        assertNotNull(found);
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(java.util.Optional.empty());

        Customer found = service.getCustomerById(id);

        assertNull(found);
    }

    @Test
    void testSaveCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer saved = service.saveCustomer(customer);

        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("john.doe@example.com", saved.getEmail());
    }

    @Test
    void testDeleteCustomer() {
        UUID id = customer.getCustomerId();
        doNothing().when(repository).deleteById(id);

        service.deleteCustomer(id);

        verify(repository, times(1)).deleteById(id);
    }
}