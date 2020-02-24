package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.*;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Only ADMINS have the right to get all the Orders
     * ADMIN - ALL Order
     * ELSE - UNAUTHORIZED
     * DISABLED USER - FORBIDDEN
     * NON EXISTING USER LOGGED IN - BAD REQUEST
     * @param auth Authentication parameter for Security in order to get the User who logged in
     * @return Returns Orders based on ROLES
     */
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

    /**
     * ADMIN - All Orders
     * DIRECTOR - Get orders if issued by, or for the company the user works at
     * MANAGER - Get orders if issued by, or for the company the user works at
     * ELSE - UNAUTHORIZED
     * ID NOT FOUND - NOT FOUND
     * DISABLED USER - FORBIDDEN
     * NON EXISTING USER LOGGED IN - BAD REQUEST
     * @param id ID of Order to return
     * @param auth Authentication parameter for Security in order to get the User who logged in
     * @return Returns Orders based on ROLES
     */
    @GetMapping("/{id}") //TODO ASAP!!! SZAR többet ad vissza
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
                        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.get().getWorkplace());
                        Map<Integer, Order> map = ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
                        if(map.get(orderToGet.get().getId()) != null){
                            return ResponseEntity.ok(map.get(orderToGet.get().getId()));
                        }
                        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                    }
                    else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                else return ResponseEntity.notFound().build();
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity getHistoriesByOrderId(@PathVariable Integer id, Authentication auth){
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (loggedInUser.isPresent()) { //If login successful
            if (loggedInUser.get().isEnabled()){
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (orderToGet.isPresent()) { //If exists
                    if (loggedInUser.get().getRole() == Role.ROLE_ADMIN){
                        return ResponseEntity.ok(orderToGet.get().getHistory());
                    }
                    else if (loggedInUser.get().getRole() == Role.ROLE_DIRECTOR || loggedInUser.get().getRole() == Role.ROLE_MANAGER) {
                        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.get().getWorkplace());
                        Map<Integer, Order> map = ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
                        if(map.get(orderToGet.get().getId()) != null){
                            return ResponseEntity.ok(map.get(orderToGet.get().getId()).getHistory());
                        }
                        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                    }
                    else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                else return ResponseEntity.notFound().build();
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * ADMIN - Get orders if issued by, or for the company the user works at (even admins)
     * DIRECTOR - Get orders if issued by, or for the company the user works at
     * MANAGER - Get orders if issued by, or for the company the user works at
     * ELSE - UNAUTHORIZED
     * ID NOT FOUND - NOT FOUND
     * DISABLED USER - FORBIDDEN
     * NON EXISTING USER LOGGED IN - BAD REQUEST
     * @param auth Authentication parameter for Security in order to get the User who logged in
     * @return Returns Orders where the user's company is a seller
     */
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

    /**
     * ADMIN - Get orders if issued by, or for the company the user works at (even admins)
     * DIRECTOR - Get orders if issued by, or for the company the user works at
     * MANAGER - Get orders if issued by, or for the company the user works at
     * ELSE - UNAUTHORIZED
     * ID NOT FOUND - NOT FOUND
     * DISABLED USER - FORBIDDEN
     * NON EXISTING USER LOGGED IN - BAD REQUEST
     * @param auth Authentication parameter for Security in order to get the User who logged in
     * @return Returns Orders where the user's company is a buyer
     */
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
