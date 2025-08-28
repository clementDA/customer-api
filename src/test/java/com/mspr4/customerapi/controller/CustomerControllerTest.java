package com.mspr4.customerapi.controller;

import com.mspr4.customerapi.model.Customer;
import com.mspr4.customerapi.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController controller;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setIsProspect(true);
    }

    @Test
    void testGetAllCustomers() {
        when(service.getAllCustomers()).thenReturn(Arrays.asList(customer));

        List<Customer> customers = controller.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("John", customers.get(0).getFirstName());
    }

    @Test
    void testGetCustomerById_Found() {
        UUID id = customer.getCustomerId();
        when(service.getCustomerById(id)).thenReturn(customer);

        ResponseEntity<Customer> response = controller.getCustomerById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        UUID id = UUID.randomUUID();
        when(service.getCustomerById(id)).thenReturn(null);

        ResponseEntity<Customer> response = controller.getCustomerById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testCreateCustomer() {
        when(service.saveCustomer(any(Customer.class))).thenReturn(customer);

        Customer created = controller.createCustomer(customer);

        assertEquals("John", created.getFirstName());
        verify(service, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_Found() {
        UUID id = customer.getCustomerId();
        Customer updated = new Customer();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("jane.smith@example.com");
        updated.setIsProspect(false);

        when(service.getCustomerById(id)).thenReturn(customer);
        when(service.saveCustomer(any(Customer.class))).thenReturn(updated);

        ResponseEntity<Customer> response = controller.updateCustomer(id, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Jane", response.getBody().getFirstName());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        UUID id = UUID.randomUUID();
        when(service.getCustomerById(id)).thenReturn(null);

        ResponseEntity<Customer> response = controller.updateCustomer(id, customer);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteCustomer_Found() {
        UUID id = customer.getCustomerId();
        when(service.getCustomerById(id)).thenReturn(customer);
        doNothing().when(service).deleteCustomer(id);

        ResponseEntity<Void> response = controller.deleteCustomer(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(service, times(1)).deleteCustomer(id);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        UUID id = UUID.randomUUID();
        when(service.getCustomerById(id)).thenReturn(null);

        ResponseEntity<Void> response = controller.deleteCustomer(id);

        assertEquals(404, response.getStatusCodeValue());
        verify(service, never()).deleteCustomer(id);
    }
}