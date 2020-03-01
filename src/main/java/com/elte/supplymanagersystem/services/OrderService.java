package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(orderRepository.findAll());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (orderToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(orderToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                //TODO van szebb megoldás?
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToGet.get().getId()) != null) {
                    return ResponseEntity.ok(map.get(orderToGet.get().getId()));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity getHistoriesByOrderId(User loggedInUser, Integer id) {
        //TODO addCreator and timestamp + listByCreatorCompany
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (orderToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(orderToGet.get().getHistory());
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToGet.get().getId()) != null) {
                    return ResponseEntity.ok(map.get(orderToGet.get().getId()).getHistory());
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity getSalesByUser(User loggedInUser){
        if(userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))){
            if(loggedInUser.getWorkplace() != null){
                List<Order> currentCompany = orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        }
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getPurchasesByUser(User loggedInUser){
        if(userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))){
            if(loggedInUser.getWorkplace() != null){
                List<Order> currentCompany = orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        }
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity putById(Order orderToUpdate, User loggedInUser, Integer id){
        orderToUpdate.setId(id);
        Optional<Order> orderToCheck = orderRepository.findById(id);
        if (orderToCheck.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(orderRepository.save(orderToUpdate));
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToUpdate.getId()) != null) {
                    return ResponseEntity.ok(orderRepository.save(map.get(orderToUpdate.getId())));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }


    private Map<Integer, Order> getMap(User loggedInUser) {
        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.getWorkplace());
        return ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
    }
}
