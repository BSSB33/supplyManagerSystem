package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.Role;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (loggedInUser.get().getRole() == Role.ROLE_ADMIN) //Mindenkit lekérdezhet
                    return ResponseEntity.ok(orderRepository.findAll());
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
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

    @GetMapping("/sales")
    public ResponseEntity getSales(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Role roleOfUser = loggedInUser.get().getRole();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                //TODO Admin should be able to get everyone's sales
                if(roleOfUser == Role.ROLE_ADMIN || roleOfUser == Role.ROLE_DIRECTOR || roleOfUser == Role.ROLE_MANAGER){
                    List<Order> currentCompany = orderRepository.findSalesByWorkplace(loggedInUser.get().getWorkplace());
                    return ResponseEntity.ok(currentCompany);
                }
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/purchases")
    public ResponseEntity getPurchases(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Role roleOfUser = loggedInUser.get().getRole();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                //TODO Admin should be able to get everyone's purchases
                if(roleOfUser == Role.ROLE_ADMIN || roleOfUser == Role.ROLE_DIRECTOR || roleOfUser == Role.ROLE_MANAGER){
                    List<Order> currentCompany = orderRepository.findPurchasesByWorkplace(loggedInUser.get().getWorkplace());
                    return ResponseEntity.ok(currentCompany);
                }
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

}
