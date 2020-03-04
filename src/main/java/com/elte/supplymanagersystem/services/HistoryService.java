package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import org.aspectj.weaver.ast.Or;
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
            Order orderToGet = historyToGet.get().getOrder();
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(historyToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                //Az kérheti le aki résztvevő a hozzátartozó order-ben, illetve aki a Creator-al egy cégnél dolgozik
                if(historyToGet.get().getCreator().getWorkplace().getId().equals(loggedInUser.getWorkplace().getId())){
                    Map<Integer, Order> map = orderService.getMap(loggedInUser);
                    if(map.get(orderToGet.getId()) != null)
                        return ResponseEntity.ok(historyToGet);
                    else return new ResponseEntity(HttpStatus.CONFLICT);
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }


}
