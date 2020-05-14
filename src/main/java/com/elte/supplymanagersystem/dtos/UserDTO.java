package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@NoArgsConstructor
@Data
public class UserDTO {

    @Size(min = 4, message = "Username length must contain at least 4 characters")
    private String username;

    @Size(min = 8, message = "Password length must contain at least 8 characters")
    private String password;

    @Size(min = 4, message = "Full Name length must contain at least 4 characters")
    private String fullName;

    private String email;

    private boolean enabled;

    private Company company;

    private List<OrderDTO> buyerManager;

    private List<OrderDTO> sellerManager;

    private Company workplace;

    private Role role;

    private List<HistoryDTO> histories;
}
