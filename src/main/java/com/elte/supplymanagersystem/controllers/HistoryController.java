package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/histories")
public class HistoryController {

    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("")
    public ResponseEntity<Iterable<History>> getAll() {
        return ResponseEntity.ok(historyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<History> get(@PathVariable Integer id) {
        Optional<History> label = historyRepository.findById(id);
        if (label.isPresent()) {
            return ResponseEntity.ok(label.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
