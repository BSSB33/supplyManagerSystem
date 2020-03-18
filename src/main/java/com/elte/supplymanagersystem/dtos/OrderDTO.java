package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@Data
@NoArgsConstructor
public class OrderDTO {

    private String productName;

    private Double price;

    private Status status;

    private List<HistoryDTO> history;

    private Company buyer;

    private User buyerManager;

    private Company seller;

    private User sellerManager;

    /**
     * Constructor for Order Data Transfer Object
     * @param order The Order object to construct the DTO of.
     */
    public OrderDTO(Order order) {
        this.productName = order.getProductName();
        this.price = order.getPrice();
        this.status = order.getStatus();
        this.buyer = order.getBuyer();
        this.buyerManager = order.getBuyerManager();
        this.seller = order.getSeller();
        this.sellerManager = order.getSellerManager();
        if (!CollectionUtils.isEmpty(order.getHistories()))
            history = order.getHistories().stream().map(HistoryDTO::new).collect(Collectors.toList());
    }
}
