package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Role;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Role roleOfUser = loggedInUser.get().getRole();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (roleOfUser == Role.ROLE_ADMIN || roleOfUser == Role.ROLE_DIRECTOR || roleOfUser == Role.ROLE_MANAGER) //Mindenkit lekérdezhet
                    return ResponseEntity.ok(companyRepository.findAll());
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        Optional<Company> companyToGet = companyRepository.findById(id);
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Role roleOfUser = loggedInUser.get().getRole();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if(companyToGet.isPresent()){
                    if (roleOfUser == Role.ROLE_ADMIN || roleOfUser == Role.ROLE_DIRECTOR || roleOfUser == Role.ROLE_MANAGER) //Mindenkit lekérdezhet
                        return ResponseEntity.ok(companyToGet.get());
                }
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }
}
