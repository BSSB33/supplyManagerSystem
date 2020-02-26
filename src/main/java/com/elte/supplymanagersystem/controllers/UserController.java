package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    //FindAll
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) { //If valid
            return userService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Find
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable @Min(0) Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) { //If valid
            return userService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //    //Save
//    @PostMapping("") //TODO same name not allowed
//    public ResponseEntity<User> post(@RequestBody User User) {
//        User userToSave = userRepository.save(User);
//        return ResponseEntity.ok(userToSave);
//    }
//
    //Save or Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody User user, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.putById(user, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
//
//    //Delete
//    @DeleteMapping("/{id}")
//    public ResponseEntity delete(@PathVariable Integer id) {
//        Optional<User> otherUser = userRepository.findById(id);
//        if (otherUser.isPresent()) {
//            userRepository.deleteById(id);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PostMapping("register")
//    public ResponseEntity<User> register(@RequestBody User user) {
//        Optional<User> userToRegister = userRepository.findByUsername(user.getUsername());
//        if (userToRegister.isPresent()) {
//            return ResponseEntity.badRequest().build();
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setEnabled(true);
//        user.setRole(Role.ROLE_USER); //TODO nem lehet default
//        return ResponseEntity.ok(userRepository.save(user));
//    }
//
//    @PostMapping("login")
//    public ResponseEntity login() {
//        return ResponseEntity.ok(authenticatedUser.getUser());
//    }


}
