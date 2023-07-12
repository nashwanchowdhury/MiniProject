package com.example.demo;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveOrder() {

        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("123 Main St");
        order.setTotal(100.0);


        Order savedOrder = orderRepository.save(order);


        assertNotNull(savedOrder.getId());
        assertEquals("John Doe", savedOrder.getCustomerName());
        assertEquals(LocalDate.now(), savedOrder.getOrderDate());
        assertEquals("123 Main St", savedOrder.getShippingAddress());
        assertEquals(100.0, savedOrder.getTotal());
    }

    @Test
    public void testFindById() {

        Order order = new Order();
        order.setCustomerName("Jane Smith");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("456 Oak St");
        order.setTotal(150.0);


        Order savedOrder = orderRepository.save(order);


        Optional<Order> retrievedOrder = orderRepository.findById(savedOrder.getId());

        assertTrue(retrievedOrder.isPresent());
        assertEquals(savedOrder.getId(), retrievedOrder.get().getId());
        assertEquals("Jane Smith", retrievedOrder.get().getCustomerName());
        assertEquals(LocalDate.now(), retrievedOrder.get().getOrderDate());
        assertEquals("456 Oak St", retrievedOrder.get().getShippingAddress());
        assertEquals(150.0, retrievedOrder.get().getTotal());
    }

    @Test
    public void testUpdateOrder() {

        Order order = new Order();
        order.setCustomerName("John Moore");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("123 Main St");
        order.setTotal(100.0);


        Order savedOrder = orderRepository.save(order);


        savedOrder.setCustomerName("Jane Smith");
        savedOrder.setTotal(200.0);


        Order updatedOrder = orderRepository.save(savedOrder);


        Optional<Order> retrievedOrder = orderRepository.findById(updatedOrder.getId());


        assertTrue(retrievedOrder.isPresent());
        assertEquals(updatedOrder.getId(), retrievedOrder.get().getId());
        assertEquals("Jane Smith", retrievedOrder.get().getCustomerName());
        assertEquals(200.0, retrievedOrder.get().getTotal());
    }

    @Test
    public void testDeleteOrder() {

        Order order = new Order();
        order.setCustomerName("Martin Luther King");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("1 Freedom Street");
        order.setTotal(10.0);


        Order savedOrder = orderRepository.save(order);


        orderRepository.delete(savedOrder);

        Optional<Order> retrievedOrder = orderRepository.findById(savedOrder.getId());


        assertFalse(retrievedOrder.isPresent());
    }
}
