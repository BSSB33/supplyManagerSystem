package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> getAll(){
        return userRepository.findAll();
    }

    public User findById(Integer id){
        Optional<User> userToGet = userRepository.findById(id);
        if(userToGet.isPresent()) return userToGet.get();
        return null;
    }

    public Iterable<User> getEmployeesOfUser(User user){
        Optional<User> userToGet = userRepository.findByUsername(user.getUsername());
        if(userToGet.isPresent()) return userToGet.get().getCompany().getManagers();
        return null;
    }

    public Iterable<User> getColleaguesOfUser(User user){
        Optional<User> userToGet = userRepository.findByUsername(user.getUsername());
        if(userToGet.isPresent()) return userToGet.get().getWorkplace().getManagers();
        return null;
    }

    public User getValidUser(String username) {
        Optional<User> loggedInUser = userRepository.findByUsername(username);
        if (loggedInUser.isPresent() && loggedInUser.get().isEnabled()) {
            User user = loggedInUser.get();
            System.out.println("User has authorities: " + user.getUsername() + " " + user.getRole());
            return user;
        }
        return null; //throws FORBIDDEN
    }

    public boolean userHasAdminRole(User user) {
        return (user.getRole() == Role.ROLE_ADMIN);
    }

    public boolean userHasDirectorRole(User user) {
        return (user.getRole() == Role.ROLE_DIRECTOR);
    }

    public boolean userHasManagerRole(User user) {
        return (user.getRole() == Role.ROLE_MANAGER);
    }

    public boolean userHasAdminOrManagerRole(User user) {
        return (user.getRole() == Role.ROLE_DIRECTOR || user.getRole() == Role.ROLE_MANAGER);
    }

    public boolean userHasDirectorOrManagerRole(User user) {
        return (user.getRole() == Role.ROLE_DIRECTOR || user.getRole() == Role.ROLE_MANAGER);
    }

    public boolean userHasRole(User user) {
        return (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_DIRECTOR || user.getRole() == Role.ROLE_MANAGER);
    }
}
