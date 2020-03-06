package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HistoryService {

    final static Logger logger = Logger.getLogger(HistoryService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryRepository historyRepository;

    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(historyRepository.findAll());
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Only those users can access the history who works in the same company as the Creator of the history,
     * and who works at one of the companies of the Order to which the History belongs to. (Plus ADMINS)
     * ADMIN: Can get ALL
     * DIRECTOR, MANAGER: Can get Data if works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: UNAUTHORIZED
     * Invalid, non existing Id: NOTFOUND
     * @param loggedInUser The user logged in
     * @param id The Id of the History the user wants to GET
     * @return Returns the a ResponseEntity with the requested History filtered by Role.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<History> historyToGet = historyRepository.findById(id);
        if (historyToGet.isPresent()) {
            Order orderToGet = historyToGet.get().getOrder();
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(historyToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                if(checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToGet.get())) {
                    return ResponseEntity.ok(historyToGet.get());
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity putById(History historyToUpdate, User loggedInUser, Integer id){
        historyToUpdate.setId(id);
        Optional<History> historyToCheck = historyRepository.findById(historyToUpdate.getId());
        if (historyToCheck.isPresent()) {
            Order orderToGet = historyToUpdate.getOrder();
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(historyRepository.save(historyToUpdate));
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                if(checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToUpdate)) {
                    return ResponseEntity.ok(historyRepository.save(historyToUpdate));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    //Add: Similar histories are allowed
    public ResponseEntity addHistory(History historyToSave, User loggedInUser) {
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(historyRepository.save(historyToSave));
        else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            historyToSave.setCreator(loggedInUser);
            Order orderToGet = historyToSave.getOrder();
            if(checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToSave)) {
                return ResponseEntity.ok(historyRepository.save(historyToSave));
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        Optional<History> historyToDelete = historyRepository.findById(id);
        if (historyToDelete.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                historyRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Order orderToGet = historyToDelete.get().getOrder();
                if (checkIfAuthorisedForHistory(loggedInUser, orderToGet, historyToDelete.get())) {
                    historyRepository.deleteById(id); //TODO Doesn't work
                    return ResponseEntity.ok().build();
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    private boolean checkIfAuthorisedForHistory(User loggedInUser, Order orderToGet, History historyToGet){
        Map<Integer, Order> map = orderService.getMap(loggedInUser);
        if(map.get(orderToGet.getId()) != null) {
            return historyToGet.getCreator().getWorkplace().getId().equals(loggedInUser.getWorkplace().getId());
        } else return false;
    }

}
