package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

//    //User = User_table
//    @Query("SELECT username FROM USERS WHERE director = directorName")
//    List<User> findEmployeesByDirectorName(String directorName);
}
