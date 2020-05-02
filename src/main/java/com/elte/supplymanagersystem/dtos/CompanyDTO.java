package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class so REST API doesn't have to send unused data
 */
@Data
@NoArgsConstructor
public class CompanyDTO {

    @Size(min = 4, message = "Name length must contain at least 4 characters")
    private String name;

    @Size(min = 4, message = "Address length must contain at least 4 characters")
    private String address;

    @Size(min = 10, message = "Tax Number length must contain at least 10 characters")
    private String taxNumber;

    @Size(min = 12, message = "Bank Account Number length must contain at least 12 characters")
    private String bankAccountNumber;

    private boolean active;

    private List<UserDTO> managers;

    private List<UserDTO> director;

    private List<OrderDTO> purchases;

    private List<OrderDTO> sales;

    private String lat;

    private String lon;

    /**
     * Constructor for Company Data Transfer Object
     *
     * @param company The Company object to construct the DTO of.
     */
    public CompanyDTO(Company company) {
        this.name = company.getName();
        this.address = company.getAddress();
        this.taxNumber = company.getTaxNumber();
        this.bankAccountNumber = company.getBankAccountNumber();
        this.active = company.isActive();
        this.lat = company.getLat();
        this.lon = company.getLon();
        if (!CollectionUtils.isEmpty(company.getManagers()))
            managers = company.getManagers().stream().map(UserDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(company.getDirector()))
            director = company.getDirector().stream().map(UserDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(company.getPurchases()))
            purchases = company.getPurchases().stream().map(OrderDTO::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(company.getSales()))
            sales = company.getSales().stream().map(OrderDTO::new).collect(Collectors.toList());
    }
}
