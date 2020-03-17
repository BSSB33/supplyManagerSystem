package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Returns All the Companies in the Database.
     * ADMIN, DIRECTOR, MANAGER: Can get ALL the Users.
     * ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the list of the Companies.
     */
    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(companyRepository.findAll());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns the Company with the requested ID.
     * ADMIN, DIRECTOR, MANAGER: Can get any the Users.
     * ELSE: UNAUTHORIZED
     * Non existing Company: NOTFOUND
     *
     * @param loggedInUser The user logged in
     * @param id           The ID of the Company the user wants to GET
     * @return Returns a ResponseEntity of the Company.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<Company> companyToGet = companyRepository.findById(id);
        if (companyToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
                return ResponseEntity.ok(companyToGet.get());
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns a ResponseEntity with the Company the User works at.
     * ADMIN, DIRECTOR, MANAGER: Can get the Company.
     * ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user logged in
     * @return Returns a ResponseEntity with the Company the User works at.
     */
    public ResponseEntity getCompanyOfLoggedInUser(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(loggedInUser.getWorkplace());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Updates the Company by ID.
     * ADMIN: Can save any of the Company.
     * DIRECTOR: Only can update its Company, else: FORBIDDEN
     * ELSE: UNAUTHORIZED
     * Non existing Company: NOTFOUND
     *
     * @param companyToUpdate The Company with the information to update.
     * @param loggedInUser    The user logged in.
     * @param id              The ID of the Company the user wants to PUT (Update).
     * @return Returns a ResponseEntity of the updated Company.
     */
    public ResponseEntity putById(Company companyToUpdate, User loggedInUser, Integer id) {
        Optional<Company> companyToCheck = companyRepository.findById(companyToUpdate.getId());
        if (companyToCheck.isPresent()) {
            companyToUpdate.setId(id);
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(companyRepository.save(companyToUpdate));
            } else if (userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                if (loggedInUser.getCompany().getId().equals(companyToUpdate.getId())) {
                    //TODO Check after OneToOne Removed
                    Optional<Company> originalDirector = companyRepository.findById(companyToUpdate.getId());
                    if (originalDirector.isPresent()) {
                        companyToUpdate.setDirector(originalDirector.get().getDirector());
                        return ResponseEntity.ok(companyRepository.save(companyToUpdate));
                    } else return ResponseEntity.ok(companyRepository.save(companyToUpdate));
                } else return new ResponseEntity(HttpStatus.FORBIDDEN);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new record of Company.
     * ADMIN: The only User who can register a company
     * ELSE: UNAUTHORIZED
     *
     * @param companyToSave The Company with the information to save.
     * @param loggedInUser  The user logged in.
     * @return Returns a ResponseEntity of the saved Company.
     */
    //Add
    public ResponseEntity addCompany(Company companyToSave, User loggedInUser) {
        Optional<Company> otherCompany = companyRepository.findByName(companyToSave.getName());
        if (otherCompany.isPresent())
            return ResponseEntity.badRequest().build();
        else {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(companyRepository.save(companyToSave));
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Deletes a Company record by ID.
     * ADMIN: The only User who can delete a company.
     * ELSE: UNAUTHORIZED
     * Non existing History: NOTFOUND
     *
     * @param id           The ID of the Company the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        Optional<Company> companyToDelete = companyRepository.findById(id);
        if (companyToDelete.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                companyRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }
}
