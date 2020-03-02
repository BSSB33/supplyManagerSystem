package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ORDER_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    public Order(Order order) {
        order.productName = productName;
        order.price = price;
        order.status = status;
        order.history = history;
        order.buyer = buyer;
        order.buyerManager = buyerManager;
        order.seller = seller;
        order.sellerManager = sellerManager;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String productName;

    @Column //Should appear only when giving an offer (frontend)
    private Double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<History> history;

    @ManyToOne
    @JoinColumn
    private Company buyer;

    @ManyToOne
    @JoinColumn
    private User buyerManager;

    @ManyToOne
    @JoinColumn
    private Company seller;

    @ManyToOne
    @JoinColumn
    private User sellerManager;
}
