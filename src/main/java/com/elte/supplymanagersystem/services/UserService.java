package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.exceptions.RegistrationException;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
        }else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getById(User loggedInUser, Integer id){
        Optional<User> userToGet = userRepository.findById(id);
        if(userToGet.isPresent()){
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(userToGet);
            else if (userHasRole(loggedInUser, new ArrayList<>(List.of(Role.ROLE_MANAGER, Role.ROLE_DIRECTOR)))) {
                if (loggedInUser.isColleague(userToGet.get()) || loggedInUser.getId().equals(id)) {
                    return ResponseEntity.ok(userToGet);
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity getUnassignedDirectors(User loggedInUser){
        if(userHasRole(loggedInUser, Role.ROLE_ADMIN)){
            return ResponseEntity.ok(userRepository.findUnassignedDirectors());
        } else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity putById(User userToSave, User loggedInUser, Integer id){
        userToSave.setId(id);
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(saveUser(userToSave));
        } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
            Optional<User> user = userRepository.findById(userToSave.getId());
            if(user.isPresent()){
                if ((userHasRole(userToSave, Role.ROLE_MANAGER) && user.get().getWorkplace().getId().equals(loggedInUser.getCompany().getId()))
                    || userToSave.getId().equals(loggedInUser.getId())) {
                    return ResponseEntity.ok(saveUser(userToSave));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return ResponseEntity.notFound().build();
        } else if (userHasRole(loggedInUser, Role.ROLE_MANAGER) && userToSave.getId().equals(loggedInUser.getId())) {
            return ResponseEntity.ok(saveUser(userToSave));
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity registerUser(User userToRegister, User loggedInUser){ //Auth, have to have workplace, DIRECTORS have to have companies
        Optional<User> otherUser = Optional.ofNullable(userRepository.findByUsername(userToRegister.getUsername()));
        if (otherUser.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        else {
            userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
            userToRegister.setEnabled(true);
            if(userHasRole(loggedInUser, Role.ROLE_ADMIN)){
                if(userHasRole(userToRegister, Role.ROLE_DIRECTOR)){
                    userToRegister.setRole(Role.ROLE_DIRECTOR);
                    userToRegister.setWorkplace(null);
                }
                return ResponseEntity.ok(userRepository.save(userToRegister));
            }
            else if(userHasRole(loggedInUser, Role.ROLE_DIRECTOR)){
                userToRegister.setRole(Role.ROLE_MANAGER);
                userToRegister.setWorkplace(loggedInUser.getCompany());
                return ResponseEntity.ok(userRepository.save(userToRegister));
            }
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    public Iterable<User> getEmployeesOfUser(User user) {
        User director = userRepository.findByUsername(user.getUsername());
        if (userHasRole(director, Role.ROLE_DIRECTOR) && director.getCompany() != null && director.getWorkplace() != null) {
            return userRepository.findByUsername(user.getUsername()).getCompany().getManagers();
        }
        else return new ArrayList<>();
    }

    public Iterable<User> getColleaguesOfUser(User user) {
        User employee = userRepository.findByUsername(user.getUsername());
        if(employee.getWorkplace() != null){
            return userRepository.findByUsername(user.getUsername()).getWorkplace().getManagers();
        }
        else return new ArrayList<>();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public ResponseEntity deleteById(Integer id, User loggedInUser){
        Optional<User> userToDelete = userRepository.findById(id);
        if(userToDelete.isPresent()){
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if(userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                if(userToDelete.get().getWorkplace().getId().equals(loggedInUser.getCompany().getId())){
                    userRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                } return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public User getValidUser(String username) {
        User loggedInUser = userRepository.findByUsername(username);
        if(loggedInUser != null){ //TODO TESTS find more nullpointer unchecked
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
