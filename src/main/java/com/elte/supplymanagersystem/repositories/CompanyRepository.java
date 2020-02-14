package com.elte.supplymanagersystem.repositories;
import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Integer> {
    Optional<Company> findByCompanyName(String companyName);
}

