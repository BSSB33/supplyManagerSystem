package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import com.elte.supplymanagersystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    //FindAll
    @GetMapping("") //Admin only
    public ResponseEntity getAll(Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName()); //TODO Consultation: Baj ha public?
        if(loggedInUser != null){ //If valid
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)){
                return ResponseEntity.ok(userService.getAll());
            }
            else if(userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)){
                return ResponseEntity.ok(userService.getEmployeesOfUser(loggedInUser));
            }
            else if(userService.userHasRole(loggedInUser, Role.ROLE_MANAGER)){
                return ResponseEntity.ok(userService.getColleaguesOfUser(loggedInUser));
            }
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        else return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //Find
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable @Min(0) Integer id, Authentication auth) {
        User loggedInUser = userService.getValidUser(auth.getName());
        if(loggedInUser != null){ //If valid
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(userService.findById(id));
            else if(userService.userHasRole(loggedInUser, new ArrayList<>(List.of(Role.ROLE_MANAGER, Role.ROLE_DIRECTOR)) )){
                User userToGet = userService.findById(id);
                if(userToGet != null){
                    if(loggedInUser.isColleague(userToGet)){ //Ha munkatárs
                        //TODO LEKÉRHETI A MUNKATÁRSAIT, DE NEM KÓDOSÍTHATJA ŐKET -> PUT
                        return ResponseEntity.ok(userToGet);
                    }
                    else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                else return ResponseEntity.notFound().build();
            }
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        else return new ResponseEntity(HttpStatus.CONFLICT);
    }

//    //Save
//    @PostMapping("") //TODO same name not allowed
//    public ResponseEntity<User> post(@RequestBody User User) {
//        User userToSave = userRepository.save(User);
//        return ResponseEntity.ok(userToSave);
//    }
//
//    //Save or Update
//    @PutMapping("/{id}")
//    public ResponseEntity put(@RequestBody User user, @PathVariable Integer id, Authentication auth) {
//        //TODO 24 MEGÍRNI MOST!
//        Optional<User> loggedInUser = userRepository.findByUsername(auth.getName());
//        Optional<User> otherUser = userRepository.findById(id);
//        if (loggedInUser.isPresent()) { //If login successful
//            if (loggedInUser.get().isEnabled()){
//                UserDetails userDetails = (UserDetails) auth.getPrincipal();
//                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());
//                user.setId(id);
//                if (loggedInUser.get().getRole() == Role.ROLE_ADMIN){
//                    //Settable Role (any)
//                    return ResponseEntity.ok(userRepository.save(user));
//                }
//                else if (loggedInUser.get().getRole() == Role.ROLE_DIRECTOR) {
//                    if(user.getRole() == Role.ROLE_MANAGER){
//                        return ResponseEntity.ok(userRepository.save(user));
//                    }
//                    else return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
//                }
//                else if(loggedInUser.get().getRole() == Role.ROLE_MANAGER){
//                    return ResponseEntity.ok(userRepository.save(loggedInUser.get()));
//                }
//                else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//            }
//            else return new ResponseEntity(HttpStatus.FORBIDDEN);
//        }
//        //TODO implemetn this branch and pother POST/PPUT Methods
//        return ResponseEntity.badRequest().build();
//    }
//
//    //Delete
//    @DeleteMapping("/{id}")
//    public ResponseEntity delete(@PathVariable Integer id) {
//        Optional<User> otherUser = userRepository.findById(id);
//        if (otherUser.isPresent()) {
//            userRepository.deleteById(id);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PostMapping("register")
//    public ResponseEntity<User> register(@RequestBody User user) {
//        Optional<User> userToRegister = userRepository.findByUsername(user.getUsername());
//        if (userToRegister.isPresent()) {
//            return ResponseEntity.badRequest().build();
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setEnabled(true);
//        user.setRole(Role.ROLE_USER); //TODO nem lehet default
//        return ResponseEntity.ok(userRepository.save(user));
//    }
//
//    @PostMapping("login")
//    public ResponseEntity login() {
//        return ResponseEntity.ok(authenticatedUser.getUser());
//    }


}
