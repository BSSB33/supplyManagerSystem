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
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (loggedInUser.isPresent()) { //If login successful
            if (loggedInUser.get().isEnabled()){
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (orderToGet.isPresent()) { //If exists
                    if (loggedInUser.get().getRole() == Role.ROLE_ADMIN)
                        return ResponseEntity.ok(orderToGet.get());
                    else if (loggedInUser.get().getRole() == Role.ROLE_DIRECTOR || loggedInUser.get().getRole() == Role.ROLE_MANAGER) {
                        List<Order> currentCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.get().getWorkplace());
                        return ResponseEntity.ok(currentCompany);
                    }
                    else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                else return ResponseEntity.notFound().build();
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/sales")
    public ResponseEntity getSales(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Role roleOfUser = loggedInUser.get().getRole();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                //TODO Admin should be able to get everyone's sales CompanyController->Orders Get("CompanyOrders/{name}")
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
