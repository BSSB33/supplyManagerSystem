package com.elte.supplymanagersystem.services;

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

    static final Logger logger = Logger.getLogger(OrderService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Only ADMINS have the right to get all the Orders.
     * ADMIN: Can get ALL the Orders.
     * MANAGER, DIRECTOR, ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user who logged in.
     * @return Returns A ResponseEntity with All the Orders based on the Role of the User who logged in.
     */
    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(orderRepository.findAll());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns the Order by ID.
     * ADMIN: Can get All the Orders.
     * DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at.
     * ELSE - UNAUTHORIZED
     * Non existing ID - NOT FOUND
     *
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the Order to return.
     * @return Returns a ResponseEntity of the Order.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (orderToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(orderToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToGet.get().getId()) != null) {
                    return ResponseEntity.ok(map.get(orderToGet.get().getId()));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns the Histories of the order with the requested ID, filtered by Role,
     * Only those users can access the history who works in the same company as the Creator of the history,
     * and who works at one of the companies of the Order to which the History belongs to. (Plus ADMINS)
     * ADMIN: Can get ALL
     * DIRECTOR, MANAGER: Can get Data if works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: UNAUTHORIZED
     * Non existing Order: NOTFOUND
     *
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the Order to get the Histories of.
     * @return Returns a ResponseEntity of the Histories.
     */
    public ResponseEntity getHistoriesByOrderId(User loggedInUser, Integer id) {
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

    /**
     * Returns the Sales of the Company the user works at.
     * ADMIN, DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at (even admins)
     * ELSE - UNAUTHORIZED
     * ID NOT FOUND - NOT FOUND
     * Unemployed user - BAD REQUEST
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the Orders where the user's company is a seller.
     */
    public ResponseEntity getSalesByUser(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            if (loggedInUser.getWorkplace() != null) {
                List<Order> currentCompany = orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns the Purchases of the Company the user works at.
     * ADMIN, DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at (even admins)
     * ELSE - UNAUTHORIZED
     * ID NOT FOUND - NOT FOUND
     * Unemployed user - BAD REQUEST
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the Orders where the user's company is a buyer.
     */
    public ResponseEntity getPurchasesByUser(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            if (loggedInUser.getWorkplace() != null) {
                List<Order> currentCompany = orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Updates the Order by ID.
     * ADMIN: Can save any of the Order.
     * DIRECTOR, MANAGER: Update Order if issued by, or for the company the user works at.
     * ELSE: UNAUTHORIZED
     * Non existing Order: NOTFOUND
     *
     * @param orderToUpdate The Order with the information to update.
     * @param loggedInUser  The user logged in.
     * @param id            The ID of the Order the user wants to PUT (Update).
     * @return Returns a ResponseEntity of the updated Order.
     */
    public ResponseEntity putById(Order orderToUpdate, User loggedInUser, Integer id) {
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

    /**
     * Creates a new record of Order.
     * ADMIN: Can add new Order without any regulations.
     * DIRECTOR, MANAGER: Can only add Order of the Company the user works at is a seller or a buyer in the Order.
     * ELSE: UNAUTHORIZED
     * Already existing Order: BAD REQUEST
     *
     * @param orderToSave  The Order with the information to save.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity of the saved Order.
     */
    //Add
    public ResponseEntity addOrder(Order orderToSave, User loggedInUser) {
        Optional<Order> otherOrder = orderRepository.findByProductName(orderToSave.getProductName());
        if (otherOrder.isPresent())
            return ResponseEntity.badRequest().build();
        else {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(orderRepository.save(orderToSave));
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                if (orderToSave.getBuyer().equals(loggedInUser.getWorkplace()) || orderToSave.getSeller().equals(loggedInUser.getWorkplace())) {
                    return ResponseEntity.ok(orderRepository.save(orderToSave));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Deletes an Order record by ID.
     * ADMIN: Can delete any Order without any regulations.
     * DIRECTOR, MANAGER:  Can only delete Order of the Company the user works at is a seller or a buyer in the Order.
     * ELSE: UNAUTHORIZED
     * Non existing Order: NOTFOUND
     *
     * @param id           The ID of the Company the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        Optional<Order> orderToDelete = orderRepository.findById(id);
        if (orderToDelete.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                orderRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToDelete.get().getId()) != null) {
                    orderRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns a Map with the orders of a company the User works at.
     * Unauthorized if the key value is null.
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a Map[Integer, Order] with the ID-s of the Orders are the keys.
     */
    public Map<Integer, Order> getMap(User loggedInUser) {
        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.getWorkplace());
        return ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
    }
}
