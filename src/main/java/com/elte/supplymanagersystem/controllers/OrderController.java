package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.dtos.HistoryDTO;
import com.elte.supplymanagersystem.dtos.OrderDTO;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.services.OrderService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.elte.supplymanagersystem.enums.ErrorMessages.UNAUTHORIZED;

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
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with All the Orders in the Database.
     */
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getAll(loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns the Order with the given ID from OrderService based on the Role of the logged in User.
     * Calls getById method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
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
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns with the Histories of an order from OrderService based on the Role of the logged in User.
     * Calls getHistoriesByOrderId method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
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
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Creates a new record of History.
     * Calls postHistoryForOrderById method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param historyDTO The History Data Transfer Object with the information to save.
     * @param id         The ID of the Order, to which the user want to add history.
     * @param auth       Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    @PostMapping("/{id}/histories")
    public ResponseEntity postHistoryForOrderById(@RequestBody HistoryDTO historyDTO, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.postHistoryForOrderById(historyDTO, loggedInUser, id);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns Company Orders where the Company is a Seller based on the Workplace of the User who logged in.
     * Calls getSalesByUser method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of Orders.
     */
    @GetMapping("/sales")
    public ResponseEntity getSales(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getSalesByUser(loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns Company Orders where the Company is a Seller based on the Workplace of the User who logged in.
     * Calls getPurchasesByUser method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of Orders.
     */
    @GetMapping("/purchases")
    public ResponseEntity getPurchases(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.getPurchasesByUser(loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Updates an Order by ID based on User Role.
     * Calls putById method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param orderDTO The order Data Transfer Object with the updated information.
     * @param id       The ID of the Order to update.
     * @param auth     Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the updated record.
     */
    //Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody OrderDTO orderDTO, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.putById(orderDTO, loggedInUser, id);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Creates a new record of Order.
     * Calls addOrder method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param orderDTO The order Data Transfer Object with the information to save.
     * @param auth     Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    //Add
    @PostMapping("")
    public ResponseEntity post(@RequestBody OrderDTO orderDTO, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return orderService.addOrder(orderDTO, loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Deletes a record by ID.
     * Calls deleteById method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
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
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }
}
