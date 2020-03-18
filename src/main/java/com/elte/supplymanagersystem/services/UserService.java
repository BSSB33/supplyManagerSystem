package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.dtos.UserDTO;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.apache.log4j.Logger;
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

    static final Logger logger = Logger.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Returns All the Users in the Database depending on the Role of the User.
     * ADMIN: Can get ALL the Users.
     * DIRECTOR: Can get only the Employees of his company.
     * MANAGER: Can get only his Colleagues.
     * ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the list of Users.
     */
    public ResponseEntity getAll(User loggedInUser) {
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(userRepository.findAll());
        } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
            return ResponseEntity.ok(getEmployeesOfUser(loggedInUser));
        } else if (userHasRole(loggedInUser, Role.ROLE_MANAGER)) {
            return ResponseEntity.ok(getColleaguesOfUser(loggedInUser));
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns the User with the Requested ID.
     * ADMIN: Can get ALL the Users.
     * DIRECTOR, MANAGER: Can only get colleagues and itself.
     * ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the User to GET.
     * @return Returns a ResponseEntity with the Requested User.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        Optional<User> userToGet = userRepository.findById(id);
        if (userToGet.isPresent()) {
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(userToGet);
            else if (userHasRole(loggedInUser, List.of(Role.ROLE_MANAGER, Role.ROLE_DIRECTOR))) {
                if (loggedInUser.getWorkplace() == null && loggedInUser.getCompany() == null && loggedInUser.getId().equals(id)) { // Doesn't work anywhere
                    return ResponseEntity.ok(loggedInUser);
                } else if (loggedInUser.isColleague(userToGet.get()) || loggedInUser.getId().equals(id)) {
                    return ResponseEntity.ok(userToGet);
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns Users who are not directors of any Companies.
     * ADMIN: Can get all.
     * ELSE: UNAUTHORIZED
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the requested Users
     */
    public ResponseEntity getUnassignedDirectors(User loggedInUser) {
        if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(userRepository.findUnassignedDirectors());
        } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Updates a User by ID.
     * ADMIN: Can Update all the users.
     * DIRECTOR: Can only Update itself and Employees.
     * MANAGER: Can only Update itself.
     *
     * @param userDTO      The user Data Transfer Object with the updated information.
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the User to Update.
     * @return Returns a ResponseEntity with the updated User.
     */
    public ResponseEntity putById(UserDTO userDTO, User loggedInUser, Integer id) {
        User userToUpdate = new User(userDTO);
        userToUpdate.setId(id);
        Optional<User> userToCheck = userRepository.findById(userToUpdate.getId());
        if (userToCheck.isPresent()) {
            //userToUpdate.getCompany().setId(userToUpdate.getCompany().getId());
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                if (userHasRole(userToUpdate, Role.ROLE_DIRECTOR))
                    userToUpdate.setWorkplace(userToUpdate.getCompany());
                return ResponseEntity.ok(userRepository.save(userToUpdate));
            } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                if ((userHasRole(userToUpdate, Role.ROLE_MANAGER) && userToCheck.get().getWorkplace().getId().equals(loggedInUser.getCompany().getId()))
                        || userToUpdate.getId().equals(loggedInUser.getId())) {
                    if (userHasRole(userToUpdate, Role.ROLE_DIRECTOR))
                        userToUpdate.setWorkplace(userToUpdate.getCompany());
                    return ResponseEntity.ok(userRepository.save(userToUpdate));
                } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else if (userHasRole(loggedInUser, Role.ROLE_MANAGER) && userToUpdate.getId().equals(loggedInUser.getId())) {
                return ResponseEntity.ok(userRepository.save(userToUpdate));
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Registers a new user to the Database.
     * ADMIN: Can register any Users with any role.
     * DIRECTOR: Can only register employees to his company.
     * MANAGER: UNAUTHORIZED
     * Already existing User: BAD REQUEST
     * Non existing User: NOTFOUND
     *
     * @param userDTO The user (Data Transfer Object) to register
     * @param loggedInUser   The user who wants to register a new User.
     * @return Returns a ResponseEntity of the saved History.
     */
    public ResponseEntity registerUser(UserDTO userDTO, User loggedInUser) {
        User userToRegister = new User(userDTO);
        Optional<User> otherUser = Optional.ofNullable(userRepository.findByUsername(userToRegister.getUsername()));
        if (otherUser.isPresent()) {
            return ResponseEntity.badRequest().build();
        } else {
            userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
            userToRegister.setEnabled(true);
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) { //Admin
                if (userHasRole(userToRegister, Role.ROLE_DIRECTOR)) {
                    userToRegister.setRole(Role.ROLE_DIRECTOR);
                    userToRegister.setWorkplace(null);
                    userToRegister.setCompany(null);
                }
                //Register Other Roles simply
                return ResponseEntity.ok(userRepository.save(userToRegister));
            } else if (loggedInUser.getWorkplace() != null && loggedInUser.getCompany() != null && userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) { //Director
                userToRegister.setRole(Role.ROLE_MANAGER);
                userToRegister.setWorkplace(loggedInUser.getCompany());
                return ResponseEntity.ok(userRepository.save(userToRegister));
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED); //Manager
        }
    }

    /**
     * Returns the Employees of the user.
     *
     * @param user The User to get Employees of.
     * @return Returns an ArrayList of Users.
     */
    public Iterable<User> getEmployeesOfUser(User user) {
        User director = userRepository.findByUsername(user.getUsername());
        if (userHasRole(director, Role.ROLE_DIRECTOR) && director.getCompany() != null && director.getWorkplace() != null) {
            return userRepository.findByUsername(user.getUsername()).getCompany().getManagers();
        } else return new ArrayList<>();
    }

    /**
     * Returns the Colleagues of the user.
     *
     * @param user The User to get Colleagues of.
     * @return Returns an ArrayList of Users.
     */
    public Iterable<User> getColleaguesOfUser(User user) {
        User employee = userRepository.findByUsername(user.getUsername());
        if (employee.getWorkplace() != null) {
            return userRepository.findByUsername(user.getUsername()).getWorkplace().getManagers();
        } else return new ArrayList<>();
    }

    /**
     * Deletes User record by ID.
     * ADMIN: Can delete any Users without any regulations.
     * DIRECTOR: Can delete only employees.
     * ELSE: UNAUTHORIZED
     * Non existing History: NOTFOUND
     *
     * @param id           The ID of the History the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            if (userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if (userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                if (userToDelete.get().getWorkplace().getId().equals(loggedInUser.getCompany().getId())) {
                    userRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            } else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Checks if the Requested user is valid and Enabled.
     *
     * @param username Name of the user to check
     * @return Returns a Valid user
     */
    public User getValidUser(String username) {
        User loggedInUser = userRepository.findByUsername(username);
        if (loggedInUser != null && loggedInUser.isEnabled()) {
            logger.debug("UserService: User has authorities: " + loggedInUser.getUsername() + " [" + loggedInUser.getRole() + "]");
            return loggedInUser;
        }
        return null; //throws FORBIDDEN
    }

    /**
     * Checks if the User has the role.
     *
     * @param user The User to check
     * @param role The Role to check.
     * @return boolean
     */
    public boolean userHasRole(User user, Role role) {
        return user.getRole() == role;
    }

    /**
     * Checks if the User has any of the listed roles.
     *
     * @param user  The User to check
     * @param roles The Roles to check.
     * @return boolean
     */
    public boolean userHasRole(User user, List<Role> roles) {
        return roles.contains(user.getRole());
    }
}
