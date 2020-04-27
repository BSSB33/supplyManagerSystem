package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@NoArgsConstructor
@Data
public class UserDTO { //TODO validate fields

    private String username;

    private String password;

    private String fullName;

    private String email;

    private boolean enabled;

    private Company company;

    private List<OrderDTO> buyerManager;

    private List<OrderDTO> sellerManager;

    private Company workplace;

    private Role role;

    private List<HistoryDTO> histories;

    /**
     * Constructor for User Data Transfer Object
     *
     * @param user The User object to construct the DTO of.
     */
    public UserDTO(User user) {
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.company = user.getCompany();
        this.workplace = user.getWorkplace();
        this.role = user.getRole();
        if (!CollectionUtils.isEmpty(user.getHistories()))
            histories = user.getHistories().stream().map(HistoryDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(user.getPurchases()))
            buyerManager = user.getPurchases().stream().map(OrderDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(user.getSells()))
            buyerManager = user.getSells().stream().map(OrderDTO::new).collect(Collectors.toList());
    }
}
