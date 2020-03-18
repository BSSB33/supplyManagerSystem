package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.HistoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class HistoryDTO {

    private User creator;

    private Order order;

    private HistoryType historyType;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public HistoryDTO(History history) {
        this.creator = history.getCreator();
        this.order = history.getOrder();
        this.historyType = history.getHistoryType();
        this.note = history.getNote();
        this.createdAt = history.getCreatedAt();
        this.updatedAt = history.getUpdatedAt();
    }
}
