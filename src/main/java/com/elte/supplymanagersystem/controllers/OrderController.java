package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("")
    public ResponseEntity<Iterable<Order>> getAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Integer id) {
        Optional<Order> label = orderRepository.findById(id);
        if (label.isPresent()) {
            return ResponseEntity.ok(label.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
