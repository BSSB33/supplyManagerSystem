package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("")
    public ResponseEntity<Iterable<Company>> getAll() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> get(@PathVariable Integer id) {
        Optional<Company> label = companyRepository.findById(id);
        if (label.isPresent()) {
            return ResponseEntity.ok(label.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO company ordereket csak az a manager/Director/admin kérlhet le aki a cégnél dolgozik/főnök a cégnél
    //a menager csak azokat az ordereket kérheti le/módosThatja/törölheti amikben a cég vagy vevő vagy eladó
}
