package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Users - < Type, ID >
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.company is empty")
    List<User> findUnassignedDirectors();

}
