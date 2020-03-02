package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.*;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.services.OrderService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

//    /**
//     * Only ADMINS have the right to get all the Orders
//     * ADMIN - ALL Order
//     * ELSE - UNAUTHORIZED
//     * DISABLED USER - FORBIDDEN
//     * NON EXISTING USER LOGGED IN - BAD REQUEST
//     * @param auth Authentication parameter for Security in order to get the User who logged in
//     * @return Returns Orders based on ROLES
//     */
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

//    /**
//     * ADMIN - All Orders
//     * DIRECTOR - Get orders if issued by, or for the company the user works at
//     * MANAGER - Get orders if issued by, or for the company the user works at
//     * ELSE - UNAUTHORIZED
//     * ID NOT FOUND - NOT FOUND
//     * DISABLED USER - FORBIDDEN
//     * NON EXISTING USER LOGGED IN - BAD REQUEST
//     * @param id ID of Order to return
//     * @param auth Authentication parameter for Security in order to get the User who logged in
//     * @return Returns Orders based on ROLES
//     */
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity getHistoriesByOrderId(@PathVariable Integer id, Authentication auth){
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getHistoriesByOrderId(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

//    /**
//     * ADMIN - Get orders if issued by, or for the company the user works at (even admins)
//     * DIRECTOR - Get orders if issued by, or for the company the user works at
//     * MANAGER - Get orders if issued by, or for the company the user works at
//     * ELSE - UNAUTHORIZED
//     * ID NOT FOUND - NOT FOUND
//     * DISABLED USER - FORBIDDEN
//     * NON EXISTING USER LOGGED IN - BAD REQUEST
//     * @param auth Authentication parameter for Security in order to get the User who logged in
//     * @return Returns Orders where the user's company is a seller
//     */
    @GetMapping("/sales")
    public ResponseEntity getSales(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getSalesByUser(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

//    /**
//     * ADMIN - Get orders if issued by, or for the company the user works at (even admins)
//     * DIRECTOR - Get orders if issued by, or for the company the user works at
//     * MANAGER - Get orders if issued by, or for the company the user works at
//     * ELSE - UNAUTHORIZED
//     * ID NOT FOUND - NOT FOUND
//     * DISABLED USER - FORBIDDEN
//     * NON EXISTING USER LOGGED IN - BAD REQUEST
//     * @param auth Authentication parameter for Security in order to get the User who logged in
//     * @return Returns Orders where the user's company is a buyer
//     */
    @GetMapping("/purchases")
    public ResponseEntity getPurchases(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getPurchasesByUser(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody Order order, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.putById(order, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Add
    @PostMapping("")
    public ResponseEntity post(@RequestBody Order order, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.addOrder(order, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
