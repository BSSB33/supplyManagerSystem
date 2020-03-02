package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryRepository historyRepository;

    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(historyRepository.findAll());
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getById(User loggedInUser, Integer id) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(historyRepository.findById(id));
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }


}
