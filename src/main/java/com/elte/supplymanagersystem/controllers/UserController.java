package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.dtos.UserDTO;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * The User Controller is responsible for: creating Endpoints and wiring the User Service
 */
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    /**
     * Returns all the Users from UserService based on the Role of the logged in User.
     * Calls getAll method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with All the Users in the Database.
     */
    //FindAll
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.getAll(loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns User by ID if the User is valid.
     * Calls getById method from OrderService.
     * Returns UNAUTHORIZED if the User is Invalid.
     *
     * @param id   The ID of the User to get.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of the User with the given ID.
     */
    //Find
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable @Min(1) Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.getById(loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns Users who are not directors.
     * Calls getUnassignedDirectors method from OrderService.
     * Returns UNAUTHORIZED if the User is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the requested Users
     */
    @GetMapping("/freeDirectors")
    public ResponseEntity getUnassignedDirectors(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.getUnassignedDirectors(loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Updates a User by ID.
     * Calls putById method from UserService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param userDTO The user Data Transfer Object with the updated information.
     * @param id      The ID of the User to update.
     * @param auth    Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the updated record.
     */
    //Save or Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody UserDTO userDTO, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.putById(userDTO, loggedInUser, id);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a new record of User.
     * Calls registerUser method from OrderService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param userDTO The User Data Transfer Object with the information to save.
     * @param auth    Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    @PostMapping("register")
    public ResponseEntity register(@RequestBody UserDTO userDTO, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.registerUser(userDTO, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Disables a User by ID.
     * Calls disableUser method from UserService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the User to disable.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity disable(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.disableUser(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Enables a User by ID.
     * Calls enableUser method from UserService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the User to disable.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity enable(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.enableUser(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Deletes a record by ID. - Deleting user is hard, it is recommended to Disable users! -
     * Calls deleteById method from UserService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the User to delete.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return userService.deleteById(id, loggedInUser);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates an authenticated User
     *
     * @return Returns an authenticatedUser with OK (200)
     */
    @PostMapping("login")
    public ResponseEntity login() {
        return ResponseEntity.ok(authenticatedUser.getUser());
    }
}
