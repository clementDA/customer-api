package com.mspr4.customerapi.controller;

import com.mspr4.customerapi.messaging.CustomerEventPublisher;
import com.mspr4.customerapi.model.Customer;
import com.mspr4.customerapi.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @Mock
    private CustomerEventPublisher eventPublisher;

    @InjectMocks
    private CustomerController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCompanyName("Reag");
    }

    @Test
    void fullEndpointTest() throws Exception {
        // GET all
        when(service.getAllCustomers()).thenReturn(Arrays.asList(customer));
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));

        // GET by id
        when(service.getCustomerById(customer.getCustomerId())).thenReturn(customer);
        mockMvc.perform(get("/api/customers/{id}", customer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        // POST
        when(service.saveCustomer(any(Customer.class))).thenReturn(customer);
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
        verify(eventPublisher, times(1)).publishCustomerCreated(any(Customer.class));

        // PUT
        Customer updated = new Customer();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("jane.smith@example.com");
        updated.setCompanyName("Dear");

        when(service.getCustomerById(customer.getCustomerId())).thenReturn(customer);
        when(service.saveCustomer(any(Customer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/customers/{id}", customer.getCustomerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
        verify(eventPublisher, times(1)).publishCustomerUpdated(any(Customer.class));

        // DELETE
        when(service.getCustomerById(customer.getCustomerId())).thenReturn(customer);
        doNothing().when(service).deleteCustomer(customer.getCustomerId());

        mockMvc.perform(delete("/api/customers/{id}", customer.getCustomerId()))
                .andExpect(status().isNoContent());
        verify(eventPublisher, times(1)).publishCustomerDeleted(any(Customer.class));
    }
}
