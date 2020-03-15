package com.elte.supplymanagersystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Companies have a Unique name.
 * Companies can be registered by Admin only.
 * Companies can trade between each other. (registration required)
 * Every Company have a director, and can have more employees.
 * Orders are assigned to companies too.
 */
@Entity
@Table(name="COMPANY_TABLE")
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

    @OneToMany(targetEntity=User.class, mappedBy="workplace", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> managers;

    @OneToOne
    @JsonIgnore
    private User director;
    
    @OneToMany(targetEntity=Order.class, mappedBy="buyer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> purchases;

    @OneToMany(targetEntity=Order.class, mappedBy="seller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> sales;
}
