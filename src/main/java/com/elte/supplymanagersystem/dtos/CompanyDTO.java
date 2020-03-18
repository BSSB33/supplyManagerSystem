package com.elte.supplymanagersystem.dtos;

import com.elte.supplymanagersystem.entities.Company;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CompanyDTO {

    private String name;

    private List<UserDTO> managers;

    private List<UserDTO> director;

    private List<OrderDTO> purchases;

    private List<OrderDTO> sales;

    public CompanyDTO(Company company) {
        this.name = company.getName();
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
