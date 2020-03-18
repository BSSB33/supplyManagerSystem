package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@NoArgsConstructor
@Data
public class UserDTO {

    private String username;

    private String password;

    private boolean enabled;

    private Company company;

    private List<OrderDTO> buyerManager;

    private List<OrderDTO> sellerManager;

    private Company workplace;

    private Role role;

    private List<HistoryDTO> histories;

    /**
     * Constructor for User Data Transfer Object
     * @param user The User object to construct the DTO of.
     */
    public UserDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.company = user.getCompany();
        this.workplace = user.getWorkplace();
        this.role = user.getRole();
        if (!CollectionUtils.isEmpty(user.getHistories()))
            histories = user.getHistories().stream().map(HistoryDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(user.getBuyerManager()))
            buyerManager = user.getBuyerManager().stream().map(OrderDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(user.getSellerManager()))
            buyerManager = user.getSellerManager().stream().map(OrderDTO::new).collect(Collectors.toList());
    }
}
