package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.dtos.HistoryDTO;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.elte.supplymanagersystem.enums.ErrorMessages.FORBIDDEN;

@Service
public class HistoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryRepository historyRepository;


    /**
     * Returns All the Histories in the Database depending on the Role of the User.
     * ADMIN: Can get ALL the Users.
     * DIRECTOR, MANAGER: Can get histories they created.
     * ELSE: FORBIDDEN
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the list of Histories.
     */
    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(historyRepository.findAll());
        else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            return ResponseEntity.ok(loggedInUser.getHistories());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Returns the History with the requested ID filtered by Role.
     * Only those users can access the history who works in the same company as the Creator of the history,
     * and who works at one of the companies of the Order to which the History belongs to. (Plus ADMINS)
     * ADMIN: Can get ALL
     * DIRECTOR, MANAGER: Can get Data if works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: FORBIDDEN
     * Non existing History: NOTFOUND
     *
     * @param loggedInUser The user logged in
     * @param id           The ID of the History the user wants to GET
     * @return Returns a ResponseEntity of the History with the requested History filtered by Role.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<History> historyToGet = historyRepository.findById(id);
        if (historyToGet.isPresent()) {
            Order orderToGet = historyToGet.get().getOrder();
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(historyToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                if (checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToGet.get())) {
                    return ResponseEntity.ok(historyToGet.get());
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new record of History.
     * ADMIN: Can add new Histories without any regulations.
     * DIRECTOR, MANAGER: Only can add History if the user works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: FORBIDDEN
     *
     * @param historyDTO   The history Data Transfer Object with the information to save.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity of the saved History.
     */
    public ResponseEntity addHistory(HistoryDTO historyDTO, User loggedInUser) {
        History historyToSave = new History(historyDTO);
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            if (historyToSave.getCreator() == null)
                historyToSave.setCreator(loggedInUser);
            return ResponseEntity.ok(historyRepository.save(historyToSave));
        } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            historyToSave.setCreator(loggedInUser);
            return ResponseEntity.ok(historyRepository.save(historyToSave));
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Deletes History record by ID.
     * ADMIN: Can delete any Histories without any regulations.
     * DIRECTOR, MANAGER: Only can delete History if the user works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: FORBIDDEN
     * Non existing History: NOTFOUND
     *
     * @param id           The ID of the History the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        Optional<History> historyToDelete = historyRepository.findById(id);
        if (historyToDelete.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                historyRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Order orderToGet = historyToDelete.get().getOrder();
                if (checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToDelete.get())) {
                    historyRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Checks if the User who logged in is authorized to modify the History record.
     *
     * @param loggedInUser The user logged in.
     * @param orderToGet   The order which contains the History
     * @param historyToGet The History to check
     * @return boolean
     */
    private boolean checkIfAuthorisedForHistory(User loggedInUser, Order orderToGet, History historyToGet) {
        Map<Integer, Order> map = orderService.getMap(loggedInUser);
        if (map.get(orderToGet.getId()) != null) {
            return historyToGet.getCreator().getWorkplace().getId().equals(loggedInUser.getWorkplace().getId());
        } else return false;
    }

}
