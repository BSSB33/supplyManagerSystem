package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends CrudRepository<History, Integer> {

    @Query("SELECT h FROM History h WHERE h.order = :order")
    List<History> findHistoriesByOrder(@Param("order") Order order);
}
