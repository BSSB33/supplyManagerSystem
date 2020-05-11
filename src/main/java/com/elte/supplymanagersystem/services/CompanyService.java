package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.dtos.CompanyDTO;
import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.elte.supplymanagersystem.enums.ErrorMessages.FORBIDDEN;

@Service
public class CompanyService {

    static final Logger logger = Logger.getLogger(CompanyService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Returns All the Companies in the Database.
     * ADMIN, DIRECTOR, MANAGER: Can get ALL the Users.
     * ELSE: FORBIDDEN
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the list of the Companies.
     */
    public ResponseEntity getAll(User loggedInUser) {
        logger.info("getAll() called");
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(companyRepository.findAll());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Returns the Company with the requested ID.
     * ADMIN, DIRECTOR, MANAGER: Can get any the Users.
     * ELSE: FORBIDDEN
     * Non existing Company: NOTFOUND
     *
     * @param loggedInUser The user logged in
     * @param id           The ID of the Company the user wants to GET
     * @return Returns a ResponseEntity of the Company.
     */
    public ResponseEntity getById(User loggedInUser, Long id) {
        logger.info("getById() called");
        Optional<Company> companyToGet = companyRepository.findById(id);
        if (companyToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
                return ResponseEntity.ok(companyToGet.get());
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns a ResponseEntity with the Company the User works at.
     * ADMIN, DIRECTOR, MANAGER: Can get the Company.
     * ELSE: FORBIDDEN
     *
     * @param loggedInUser The user logged in
     * @return Returns a ResponseEntity with the Company the User works at.
     */
    public ResponseEntity getCompanyOfLoggedInUser(User loggedInUser) {
        logger.info("getCompanyOfLoggedInUser() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))
                && loggedInUser.getWorkplace() != null) {
            return ResponseEntity.ok(loggedInUser.getWorkplace());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Updates the Company by ID.
     * ADMIN: Can save any of the Company.
     * DIRECTOR: Only can update its Company, else: FORBIDDEN
     * ELSE: FORBIDDEN
     * Non existing Company: NOTFOUND
     *
     * @param companyDTO   The Company Data Transfer Object with the information to update.
     * @param loggedInUser The user logged in.
     * @param id           The ID of the Company the user wants to PUT (Update).
     * @return Returns a ResponseEntity of the updated Company.
     */
    public ResponseEntity putById(CompanyDTO companyDTO, User loggedInUser, Long id) {
        logger.info("putById() called");
        Company companyToUpdate = new Company(companyDTO);
        companyToUpdate.setId(id);

        Optional<Company> companyToCheck = companyRepository.findById(companyToUpdate.getId());
        if (companyToCheck.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(companyRepository.save(companyToUpdate));
            } else if (userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                if (loggedInUser.getCompany().getId().equals(companyToUpdate.getId())) {
                    Optional<Company> originalDirector = companyRepository.findById(companyToUpdate.getId());
                    originalDirector.ifPresent(company -> companyToUpdate.setDirector(company.getDirector()));
                    return ResponseEntity.ok(companyRepository.save(companyToUpdate));
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new record of Company.
     * ADMIN: The only User who can register a company
     * ELSE: FORBIDDEN
     *
     * @param companyDTO   The Company Data Transfer Object with the information to save.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity of the saved Company.
     */
    //Add
    public ResponseEntity addCompany(CompanyDTO companyDTO, User loggedInUser) {
        logger.info("addCompany() called");
        Company companyToSave = new Company(companyDTO);
        Optional<Company> otherCompany = companyRepository.findByName(companyToSave.getName());
        if (otherCompany.isPresent())
            return ResponseEntity.badRequest().build();
        else {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)){
                companyToSave.setActive(true);
                return ResponseEntity.ok(companyRepository.save(companyToSave));
            }
            else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        }
    }

    /**
     * Enables a Company by ID.
     * ADMIN: Can enable any Company without any regulations.
     * Non existing User: NOTFOUND
     * ELSE: Forbidden
     *
     * @param id           The ID of the Company the admin wants to enable.
     * @param loggedInUser The user logged in (admin).
     * @return Returns a ResponseEntity: OK if the operation was successful and NotFound if the record was not found.
     */
    public ResponseEntity enableCompany(Long id, User loggedInUser) {
        logger.info("enableCompany() called");
        Optional<Company> companyToEnable = companyRepository.findById(id);
        if (companyToEnable.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                for (User userToEnable : companyToEnable.get().getManagers()){
                    userService.enableUser(userToEnable.getId(), loggedInUser);
                }
                companyToEnable.get().setActive(true);
                return ResponseEntity.ok(companyRepository.save(companyToEnable.get()));
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Disables a Company by ID.
     * ADMIN: Can disable any Company without any regulations.
     * Non existing User: NOTFOUND
     * ELSE: Forbidden
     *
     * @param id           The ID of the Company the admin wants to disable.
     * @param loggedInUser The user logged in (admin).
     * @return Returns a ResponseEntity: OK if the operation was successful and NotFound if the record was not found.
     */
    public ResponseEntity disableCompany(Long id, User loggedInUser) {
        logger.info("disableCompany() called");
        Optional<Company> companyToDisable = companyRepository.findById(id);
        if (companyToDisable.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                for (User userToDisable : companyToDisable.get().getManagers()){
                    userService.disableUser(userToDisable.getId(), loggedInUser);
                }
                companyToDisable.get().getSales().forEach(sale -> {
                    sale.setArchived(true);
                    orderRepository.save(sale);
                });
                companyToDisable.get().getPurchases().forEach(purchase -> {
                    purchase.setArchived(true);
                    orderRepository.save(purchase);
                });
                companyToDisable.get().setActive(false);
                return ResponseEntity.ok(companyRepository.save(companyToDisable.get()));
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Checks if the Company has any relations to other objects.
     *
     * @param companyToDelete The Company to check
     * @return boolean
     */
    private boolean isDeletable(Company companyToDelete) {
        return companyToDelete.getManagers().isEmpty() && companyToDelete.getPurchases().isEmpty() &&
                companyToDelete.getSales().isEmpty() && companyToDelete.getDirector().isEmpty();
    }

    /**
     * Deletes a Company record by ID.
     * ADMIN: The only User who can delete a company.
     * NOT_ACCEPTABLE: If tries to delete Company with any Order, Manager.
     * If the Company has Managers or Orders, then cannot be deleted: NOT_ACCEPTABLE is thrown,
     * and the connected Managers, Purchases, Sales, Directors are returned.
     * ELSE: UNAUTHORIZED
     * Non existing History: NOTFOUND
     *
     * @param id           The ID of the Company the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Remove
    public ResponseEntity deleteById(Long id, User loggedInUser) {
        logger.info("deleteById() called");
        Optional<Company> companyToDelete = companyRepository.findById(id);
        if (companyToDelete.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                if (isDeletable(companyToDelete.get())) {
                    companyRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                } else {
                    Set<Object> remainingObjects = new HashSet<>();
                    remainingObjects.addAll(companyToDelete.get().getDirector());
                    remainingObjects.addAll(companyToDelete.get().getManagers());
                    remainingObjects.addAll(companyToDelete.get().getPurchases());
                    remainingObjects.addAll(companyToDelete.get().getSales());
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            "Record Cannot Be Deleted, because it is still relationally connected the following objects: "
                                    + remainingObjects);
                }
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }
}
