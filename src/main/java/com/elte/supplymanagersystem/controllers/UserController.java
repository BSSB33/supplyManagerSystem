package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Role;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    //FindAll
    @GetMapping("") //Admin only + Register
    public ResponseEntity getAll(Authentication auth) {
        Optional<User> user = userRepository.findByUsername(auth.getName());
        if(user.isPresent()){
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            if(user.get().getRole() == Role.ROLE_ADMIN){
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());
                return ResponseEntity.ok(userRepository.findAll());
            }
            else {
                System.out.println("User has authorities: " + userDetails.getUsername() + " " + userDetails.getAuthorities());
                return ResponseEntity.notFound().build(); //TODO check whether wrongly denies access?
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    //Find
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable @Min(0) Integer id) {
        Optional<User> userToGet = userRepository.findById(id);
        if (userToGet.isPresent()) {
            return ResponseEntity.ok(userToGet.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Save
    @PostMapping("")
    public ResponseEntity<User> post(@RequestBody User User) {
        User userToSave = userRepository.save(User);
        return ResponseEntity.ok(userToSave);
    }

    //Save or Update
    @PutMapping("/{id}")
    public ResponseEntity<User> put(@RequestBody User User, @PathVariable Integer id) {
        Optional<User> otherUser = userRepository.findById(id);
        if (otherUser.isPresent()) {
            User.setId(id);
            return ResponseEntity.ok(userRepository.save(User));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        Optional<User> otherUser = userRepository.findById(id);
        if (otherUser.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody User user) {
        Optional<User> userToRegister = userRepository.findByUsername(user.getUsername());
        if (userToRegister.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRole(Role.ROLE_USER); //TODO nem lehet default
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("login")
    public ResponseEntity login() {
        return ResponseEntity.ok(authenticatedUser.getUser());
    }


}
