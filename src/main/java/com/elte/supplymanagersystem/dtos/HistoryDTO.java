package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.HistoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@Data
@NoArgsConstructor
public class HistoryDTO {

    private User creator;

    private Order order;

    private HistoryType historyType;

    private String note;

    private LocalDateTime createdAt;

    /**
     * Constructor for History Data Transfer Object
     *
     * @param history The History object to construct the DTO of.
     */
    public HistoryDTO(History history) {
        this.creator = history.getCreator();
        this.order = history.getOrder();
        this.historyType = history.getHistoryType();
        this.note = history.getNote();
        this.createdAt = history.getCreatedAt();
    }
}
