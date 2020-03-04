package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    final static Logger logger = Logger.getLogger(OrderService.class);

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
                    ArrayList<History> authorizedHistories = new ArrayList<>();
                    orderToGet.get().getHistory().stream().filter(history ->
                            history.getCreator().getWorkplace().getId().equals(loggedInUser.getWorkplace().getId())).
                            forEach(authorizedHistories::add); //KIEMELNI/HIGHLIGHT
                    return ResponseEntity.ok(authorizedHistories);
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
                    return ResponseEntity.ok(orderRepository.save(orderToUpdate));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    //Add
    public ResponseEntity addOrder(Order orderToSave, User loggedInUser){
        Optional<Order> otherOrder = orderRepository.findByProductName(orderToSave.getProductName());
        if (otherOrder.isPresent())
            return ResponseEntity.badRequest().build();
        else {
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(orderRepository.save(orderToSave));
            else if(userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))){
                if(orderToSave.getBuyer().equals(loggedInUser.getWorkplace()) || orderToSave.getSeller().equals(loggedInUser.getWorkplace())){
                    return ResponseEntity.ok(orderRepository.save(orderToSave)); //TODO bug
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser){
        Optional<Order> orderToDelete = orderRepository.findById(id);
        if (orderToDelete.isPresent()){
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)){
                orderRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if(userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))){
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToDelete.get().getId()) != null) {
                    orderRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }


    public Map<Integer, Order> getMap(User loggedInUser) {
        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.getWorkplace());
        return ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
    }

    public List<Order> getAllOrderByWorkplace(Company company){
        return orderRepository.findAllOrderByWorkplace(company);
    }
}
