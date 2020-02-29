package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(companyRepository.findAll());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<Company> companyToGet = companyRepository.findById(id);
        if(companyToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
                return ResponseEntity.ok(companyToGet.get());
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

}
