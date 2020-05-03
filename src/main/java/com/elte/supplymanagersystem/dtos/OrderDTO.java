package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@Data
@NoArgsConstructor
public class OrderDTO {

    @Size(min = 3, message = "Product Name length must contain at least 3 characters")
    private String productName;

    @Size(min = 2, message = "Price length must contain at least 2 characters")
    private Double price;

    private Status status;

    private boolean isArchived;

    private List<HistoryDTO> history;

    private Company buyer;

    private User buyerManager;

    private Company seller;

    private User sellerManager;

    private LocalDate createdAt;

    private LocalDate modifiedAt;

    private String description;

}
