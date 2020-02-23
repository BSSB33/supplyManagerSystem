package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    Optional<Order> findByProductName(String productName);

    @Query("SELECT o FROM Order o WHERE o.seller = :company")
    List<Order> findSalesByWorkplace(@Param("company") Company company);

    @Query("SELECT o FROM Order o WHERE o.buyer = :company")
    List<Order> findPurchasesByWorkplace(@Param("company") Company company);
}
