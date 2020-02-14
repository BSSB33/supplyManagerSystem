package com.elte.supplymanagersystem.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String productName;


    @ManyToOne
    @JoinColumn
    private Company buyer;

    @Column(nullable = false)
    private User buyerManager;

    @ManyToOne
    @JoinColumn
    private Company seller;

    @Column(nullable = false)
    private User sellerManager;


    @Column //Should appear only when giving an offer
    private Double price;

    @Column(nullable = false)
    private Status status;

    @Column
    private History history;


}
