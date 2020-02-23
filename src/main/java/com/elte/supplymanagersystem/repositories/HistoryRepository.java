package com.elte.supplymanagersystem.repositories;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends CrudRepository<History, Integer> {
    //TODO Optional<History> findHistoryType(History.HistoryType historyType);

    @Query("SELECT h FROM History h WHERE h.order = :order")
    List<History> findHistoriesByOrder(@Param("order") Order order/*, @Param("workplace") Company workplace*/);
}
