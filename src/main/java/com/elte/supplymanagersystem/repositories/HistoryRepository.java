package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Histories: Type, ID
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    @Query("SELECT h FROM History h WHERE h.order = :order")
    List<History> findHistoriesByOrder(@Param("order") Order order);
    
    @Modifying
    @Query("DELETE FROM History h WHERE h.id = :id")
    void deleteById(@Param("id") Integer id);
}
