package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.services.HistoryService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/histories")
public class HistoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody History history, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.putById(history, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Add
    @PostMapping("")
    public ResponseEntity post(@RequestBody History history, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.addHistory(history, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

}
