package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    Optional<Order> findByProductName(String productName);

    @Query("SELECT o FROM Order o WHERE o.seller = :workplace")
    List<Order> findSalesByWorkplace(@Param("workplace") Company workplace);

    @Query("SELECT o FROM Order o WHERE o.buyer = :workplace")
    List<Order> findPurchasesByWorkplace(@Param("workplace") Company workplace);

    @Query("SELECT o FROM Order o WHERE o.buyer = :workplace OR o.seller = :workplace") //TODO  Check if OR woks fine -> examples
    List<Order> findAllOrderByWorkplace(@Param("workplace") Company workplace);
}
