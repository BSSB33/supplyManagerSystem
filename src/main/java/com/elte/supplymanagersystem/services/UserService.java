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
import java.util.ArrayList;
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
        return userToGet.orElse(null);
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
            System.out.println("User has authorities: " + user.getUsername() + " [" + user.getRole() + "]");
            return user;
        }
        return null; //throws FORBIDDEN
    }

    public boolean userHasRole(User user, Role role){
        return user.getRole() == role;
    }

    public boolean userHasRole(User user, ArrayList<Role> roles){
        return roles.contains(user.getRole());
    }
}
