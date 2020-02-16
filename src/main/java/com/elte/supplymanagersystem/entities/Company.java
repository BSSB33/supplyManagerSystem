package com.elte.supplymanagersystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @Column(nullable = false)
    private String name;

    @OneToMany(targetEntity=User.class, mappedBy="workplace")
    @JsonIgnore
    private List<User> managers;

    @OneToOne
    @JoinColumn
    private User director;
    
    @OneToMany(targetEntity=Order.class, mappedBy="buyer")
    @JsonIgnore
    private List<Order> purchases;

    @OneToMany(targetEntity=Order.class, mappedBy="seller")
    @JsonIgnore
    private List<Order> sales;

}
