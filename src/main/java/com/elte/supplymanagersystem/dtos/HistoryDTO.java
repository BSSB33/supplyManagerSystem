package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.HistoryType;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime createdAt;

}
