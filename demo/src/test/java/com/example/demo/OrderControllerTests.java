package com.example.demo;

import com.example.demo.controller.OrderController;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
public class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    public void testCreateOrder() throws Exception {

        Order order = new Order();
        order.setCustomerName("Peter Griffin");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("29 Spooner Street");
        order.setTotal(50.0);


        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });


        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\": \"Peter Griffin\", \"shippingAddress\": \"29 Spooner Street\", \"total\": 50.0}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }


    @Test
    public void testFindOrderById() throws Exception {

        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Joe Swanson");
        order.setOrderDate(LocalDate.parse("2023-07-03"));
        order.setShippingAddress("30 Spooner Street");
        order.setTotal(20.0);


        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName", Matchers.is("Peter Griffin")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDate", Matchers.is("2023-07-03")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shippingAddress", Matchers.is("29 Spooner Street")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.is(50.0)));
    }


    @Test
    public void testUpdateOrder() throws Exception {

        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Rey Mysterio");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("2 Brooklyn Drive");
        order.setTotal(10.0);


        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);


        mockMvc.perform(MockMvcRequestBuilders.put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\": \"Rey Mysterio\", \"shippingAddress\": \"2 Brooklyn Drive\", \"total\": 10.0}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("Rey Mysterio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shippingAddress").value("2 Brooklyn Drive"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(10.0));
    }

    @Test
    public void testDeleteOrder() throws Exception {

        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("John Cena");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("4 First Street");
        order.setTotal(60.0);

        // Mock the repository behavior
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));

        // Perform DELETE request to delete the order
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testCreateOrderWithValidationErrors() throws Exception {

        Order order = new Order();
        order.setCustomerName("");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("");
        order.setTotal(-10.0);


        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\": \"\", \"shippingAddress\": \"\", \"total\": -10.0}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Name cannot be empty")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Address cannot be empty")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("The total needs be greater than 0")));
    }

    @Test
    public void testUpdateNonexistentOrder() throws Exception {
        // Prepare mock order
        Order order = new Order();
        order.setId(1L); // Existing order ID
        order.setCustomerName("Dwayne Johnson");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("5 Fifth Street");
        order.setTotal(10.0);

        // Mock the repository behavior
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // Perform PUT request to update the order
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\": \"Dwayne Johnson\", \"shippingAddress\": \"5 Fifth Street\", \"total\": 10.0}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteNonexistentOrder() throws Exception {
        // Prepare mock order
        Order order = new Order();
        order.setId(10L); // Nonexistent order ID

        // Mock the repository behavior
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // Perform DELETE request to delete the order
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}

