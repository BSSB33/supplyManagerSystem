package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    //findByBuyer, finBySeller ect...
}
