package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    public ResponseEntity getAll(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(companyRepository.findAll());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<Company> companyToGet = companyRepository.findById(id);
        if(companyToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
                return ResponseEntity.ok(companyToGet.get());
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity getCompanyOfLoggedInUser(User loggedInUser) {
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
            return ResponseEntity.ok(loggedInUser.getWorkplace());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity putById(Company companyToUpdate, User loggedInUser, Integer id){
        companyToUpdate.setId(id);
        if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)){
            return ResponseEntity.ok(companyRepository.save(companyToUpdate));
        } else if(userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)){
            if( loggedInUser.getCompany().getId().equals(companyToUpdate.getId()) ){
                Optional<Company> originalDirector = companyRepository.findById(companyToUpdate.getId());
                if(originalDirector.isPresent()){
                    companyToUpdate.setDirector(originalDirector.get().getDirector());
                    return ResponseEntity.ok(companyRepository.save(companyToUpdate));
                } else return ResponseEntity.ok(companyRepository.save(companyToUpdate));
            } else return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    //Add
    public ResponseEntity addCompany(Company companyToSave, User loggedInUser){
        Optional<Company> otherCompany = companyRepository.findByName(companyToSave.getName());
        if (otherCompany.isPresent())
            return ResponseEntity.badRequest().build();
        else {
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(companyRepository.save(companyToSave));
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser){
        Optional<Company> companyToDelete = companyRepository.findById(id);
        if (companyToDelete.isPresent()){
            if(userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)){
                companyRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }
}
