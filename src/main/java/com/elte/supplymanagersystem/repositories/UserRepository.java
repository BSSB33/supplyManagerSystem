package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Users: Type, ID
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.company is empty")
    List<User> findUnassignedDirectors();

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteById(@Param("id") Long id);
}
