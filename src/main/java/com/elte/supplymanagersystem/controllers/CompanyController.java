package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.dtos.CompanyDTO;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.services.CompanyService;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.elte.supplymanagersystem.enums.ErrorMessages.UNAUTHORIZED;

/**
 * The Company Controller is responsible for: creating Endpoints and wiring User and Company Services
 */
@CrossOrigin
@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    /**
     * Returns all the Companies from CompanyService based on the Role of the logged in User.
     * Calls getAll method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with All the Companies in the Database.
     */
    @GetMapping("")
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.getAll(loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns the Company with the given ID from CompanyService based on the Role of the logged in User.
     * Calls getById method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the Company to get.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity of the Company with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.getById(loggedInUser, id);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Returns the Workplace of the logged in User with the help of CompanyService.
     * Calls getCompanyOfLoggedInUser method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the Company of the user who logged in.
     */
    @GetMapping("/mycompany")
    public ResponseEntity getCompanyOfLoggedInUser(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.getCompanyOfLoggedInUser(loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Updates a Company by ID based on User Role.
     * Calls putById method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param companyDTO The Company with the updated information.
     * @param id         The ID of the Company to update.
     * @param auth       Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the updated record.
     */
    //Update
    @PutMapping("/{id}")
    public ResponseEntity put(@RequestBody CompanyDTO companyDTO, @PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.putById(companyDTO, loggedInUser, id);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Creates a new record of Company.
     * Calls addCompany method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param companyDTO The company with the information to save.
     * @param auth       Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity with the saved record.
     */
    //Add
    @PostMapping("register")
    public ResponseEntity post(@RequestBody CompanyDTO companyDTO, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.addCompany(companyDTO, loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }

    /**
     * Deletes a record by ID.
     * Calls deleteById method from CompanyService.
     * Returns UNAUTHORIZED if the user is Invalid.
     *
     * @param id   The ID of the Company to delete.
     * @param auth Authentication parameter for Security in order to get the User who logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if (loggedInUser != null) {
            return companyService.deleteById(id, loggedInUser);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
    }
}
