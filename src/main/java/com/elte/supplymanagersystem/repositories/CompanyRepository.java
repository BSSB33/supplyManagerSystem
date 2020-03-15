package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Companies - < Type, ID >
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByName(String name);

    Optional<Company> findById(Integer id);
}

