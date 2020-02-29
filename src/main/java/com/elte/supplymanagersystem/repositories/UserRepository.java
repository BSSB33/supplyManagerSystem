package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.company is empty")
    List<User> findUnassignedDirectors();

}
