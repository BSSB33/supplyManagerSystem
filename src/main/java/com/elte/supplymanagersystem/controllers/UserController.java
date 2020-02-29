package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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
    public ResponseEntity get(@PathVariable @Min(1) Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) { //If valid
            return userService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Save or Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody User user, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.putById(user, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Delete
    @DeleteMapping("/{id}") //TODO Doesn't work
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @PostMapping("register")
    public ResponseEntity register(@RequestBody User user, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.registerUser(user, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @PostMapping("login")
    public ResponseEntity login() {
        return ResponseEntity.ok(authenticatedUser.getUser());
    }
}
