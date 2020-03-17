package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.services.OrderService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * The Order Controller is responsible for: creating Endpoints and wiring User and Order Services
 */
@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * Returns all the Orders from OrderService based on the Role of the logged in User.
     * Calls getAll method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with All the Orders in the Database.
     */
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Returns the Order with the given ID from OrderService based on the Role of the logged in User.
     * Calls getById method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param id   The ID of the Order to get.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of the Order with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Returns with the Histories of an order from OrderService based on the Role of the logged in User.
     * Calls getHistoriesByOrderId method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param id   The ID of the Order to get the Histories of.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the history of the requested Order to which the user is authorized.
     */
    @GetMapping("/{id}/histories")
    public ResponseEntity getHistoriesByOrderId(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getHistoriesByOrderId(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Returns Company Orders where the Company is a Seller based on the Workplace of the User who logged in.
     * Calls getSalesByUser method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of Orders.
     */
    @GetMapping("/sales")
    public ResponseEntity getSales(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getSalesByUser(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Returns Company Orders where the Company is a Seller based on the Workplace of the User who logged in.
     * Calls getPurchasesByUser method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of Orders.
     */
    @GetMapping("/purchases")
    public ResponseEntity getPurchases(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getPurchasesByUser(loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Updates an Order by ID based on User Role.
     * Calls putById method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param order The order with the updated information.
     * @param id    The ID of the Order to update.
     * @param auth  Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the updated record.
     */
    //Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody Order order, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.putById(order, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Creates a new record of Order.
     * Calls addOrder method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param order The order with the information to save.
     * @param auth  Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    //Add
    @PostMapping("")
    public ResponseEntity post(@RequestBody Order order, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.addOrder(order, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    /**
     * Deletes a record by ID.
     * Calls deleteById method from OrderService.
     * Returns FORBIDDEN if the user is Invalid.
     *
     * @param id   The ID of the Order to delete.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
