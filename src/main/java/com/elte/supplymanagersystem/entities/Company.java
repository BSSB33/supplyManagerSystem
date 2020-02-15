package com.elte.supplymanagersystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
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

    @Column
    private User director;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<User> managers;
    
    @OneToMany(mappedBy = "buyer")
    @JsonIgnore
    private List<Order> purchases;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<Order> sales;

}
