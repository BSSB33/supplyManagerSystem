package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @GetMapping("")
    public ResponseEntity<Iterable<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Integer id) {
        Optional<User> label = userRepository.findById(id);
        if (label.isPresent()) {
            return ResponseEntity.ok(label.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
