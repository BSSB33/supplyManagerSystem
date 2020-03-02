package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.services.HistoryService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
//        if (loggedInUser.get().getRole() == Role.ROLE_ADMIN){
//            return ResponseEntity.ok(historyToGet.get());
//        }
//        else if (loggedInUser.get().getRole() == Role.ROLE_DIRECTOR || loggedInUser.get().getRole() == Role.ROLE_MANAGER) {
//            List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.get().getWorkplace());
//            Map<Integer, Order> map = ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
//            if(map.get(historyToGet.get().getId()) != null){
//                return ResponseEntity.ok(map.get(historyToGet.get().getId()).getHistory());
//            }
//            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//        }
//        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
