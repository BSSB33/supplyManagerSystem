package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.dtos.HistoryDTO;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.services.HistoryService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * The History Controller is responsible for: creating Endpoints and wiring User and History Services
 */
@CrossOrigin
@RestController
@RequestMapping("/histories")
public class HistoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    /**
     * Returns all the Histories from HistoryService based on the Role of the logged in User.
     * Calls getAll method from HistoryService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with All the Histories in the Database.
     */
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns the History with the given ID from HistoryService based on the Role of the logged in User.
     * Calls getById method from HistoryService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the History to get.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of the History with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a new record of History.
     * Calls addHistory method from HistoryService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param historyDTO The History Data Transfer Object with the information to save.
     * @param auth       Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    //Add
    @PostMapping("")
    public ResponseEntity post(@RequestBody HistoryDTO historyDTO, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.addHistory(historyDTO, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Deletes a record by ID.
     * Calls deleteById method from HistoryService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the History to delete.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return historyService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
