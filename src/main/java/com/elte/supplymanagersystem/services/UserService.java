package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public ResponseEntity getAll(User loggedInUser) {
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(findAll());
        } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
            return ResponseEntity.ok(getEmployeesOfUser(loggedInUser));
        } else if (userHasRole(loggedInUser, Role.ROLE_MANAGER)) {
            return ResponseEntity.ok(getColleaguesOfUser(loggedInUser));
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public User findById(Integer id) {
        Optional<User> userToGet = userRepository.findById(id);
        return userToGet.orElse(null);
    }

    public ResponseEntity getById(User loggedInUser, Integer id){
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(findById(id));
        else if (userHasRole(loggedInUser, new ArrayList<>(List.of(Role.ROLE_MANAGER, Role.ROLE_DIRECTOR)))) {
            User userToGet = findById(id);
            if (userToGet != null) {
                if (loggedInUser.isColleague(userToGet)) {
                    return ResponseEntity.ok(userToGet);
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return ResponseEntity.notFound().build();
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity putById(User userToSave, User loggedInUser, Integer id){
        userToSave.setId(id);
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(saveUser(userToSave));
        } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
            if ((userHasRole(userToSave, Role.ROLE_MANAGER) && userToSave.getWorkplace().getId().equals(loggedInUser.getCompany().getId()))
                    || userToSave.getId().equals(loggedInUser.getId())) {
                return ResponseEntity.ok(saveUser(userToSave));
            } else return new ResponseEntity(HttpStatus.CONFLICT);
        } else if (userHasRole(loggedInUser, Role.ROLE_MANAGER) && userToSave.getId().equals(loggedInUser.getId())) {
            return ResponseEntity.ok(saveUser(userToSave));
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public Iterable<User> getEmployeesOfUser(User user) {
        return userRepository.findByUsername(user.getUsername()).getCompany().getManagers();
    }

    public Iterable<User> getColleaguesOfUser(User user) {
        return userRepository.findByUsername(user.getUsername()).getWorkplace().getManagers();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getValidUser(String username) {
        User loggedInUser = userRepository.findByUsername(username);
        if(loggedInUser != null){ //TODO find more nullpointer unchecked
            if (loggedInUser.isEnabled()) {
                System.out.println("User has authorities: " + loggedInUser.getUsername() + " [" + loggedInUser.getRole() + "]");
                return loggedInUser;
            }
        }
        return null; //throws FORBIDDEN
    }

    public boolean userHasRole(User user, Role role) {
        return user.getRole() == role;
    }

    public boolean userHasRole(User user, List<Role> roles) {
        return roles.contains(user.getRole());
    }
}
