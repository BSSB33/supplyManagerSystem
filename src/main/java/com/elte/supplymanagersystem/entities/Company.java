package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.dtos.CompanyDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Companies have a Unique name.
 * Companies can be registered by Admin only.
 * Companies can trade between each other. (registration required)
 * Every Company can have multiple directors, and can have more employees.
 * Orders are assigned to companies too.
 */
@Entity
@Table(name = "COMPANY_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "workplace")//, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> managers;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "company")//, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> director;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "buyer")//, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> purchases;

    @OneToMany(mappedBy = "seller")//, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> sales;

    /**
     * Constructor for constructing Company object from DTO Object
     *
     * @param companyDTO The DTO to construct from.
     */
    public Company(CompanyDTO companyDTO) {
        this.name = companyDTO.getName();
        if (!CollectionUtils.isEmpty(companyDTO.getManagers()))
            managers = companyDTO.getManagers().stream().map(User::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(companyDTO.getDirector()))
            director = companyDTO.getDirector().stream().map(User::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(companyDTO.getPurchases()))
            purchases = companyDTO.getPurchases().stream().map(Order::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(companyDTO.getSales()))
            sales = companyDTO.getSales().stream().map(Order::new).collect(Collectors.toList());
    }
}
