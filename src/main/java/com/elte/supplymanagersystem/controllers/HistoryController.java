package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/histories")
public class HistoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        if (loggedInUser.isPresent()) {
            if (loggedInUser.get().isEnabled()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (loggedInUser.get().getRole() == Role.ROLE_ADMIN) //Mindenkit lekérdezhet
                    return ResponseEntity.ok(historyRepository.findAll());
                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
        Optional<History> historyToGet = historyRepository.findById(id);
        if (loggedInUser.isPresent()) { //If login successful
            if (loggedInUser.get().isEnabled()){
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());

                if (historyToGet.isPresent()) { //If exists
                    if (loggedInUser.get().getRole() == Role.ROLE_ADMIN){
                        return ResponseEntity.ok(historyToGet.get());
                    }
                    else if (loggedInUser.get().getRole() == Role.ROLE_DIRECTOR || loggedInUser.get().getRole() == Role.ROLE_MANAGER) {
                        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.get().getWorkplace());
                        Map<Integer, Order> map = ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
                        if(map.get(historyToGet.get().getId()) != null){
                            return ResponseEntity.ok(map.get(historyToGet.get().getId()).getHistory());
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

}
