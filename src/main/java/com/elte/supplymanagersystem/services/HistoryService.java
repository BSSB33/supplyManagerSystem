package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional<History> historyToGet = historyRepository.findById(id);
        if (historyToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(historyToGet.get());
//            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
//                //TODO: az kérheti le aki résztvevő a hozzátartozó order-ben, illetve aki a Creator-al egy cégnél dolgozik
//                //Map-es és filteres megoldás
//                if (map.get(historyToGet.get().getId()) != null) {
//                    return ResponseEntity.ok(map.get(historyToGet.get().getId()));
//                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//            }
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }


}
