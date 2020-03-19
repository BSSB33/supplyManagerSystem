package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.dtos.OrderDTO;
import com.elte.supplymanagersystem.enums.Status;
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
 * Orders are the main blocks of the system.
 * Directors and Managers can give offers for Different Companies.
 * An offer can contain a Product name, the price of the product.
 * Additional tools for tracking the Status of the Order
 * Status: Can be switched to different states, like: ORDERED, UNDER_SHIPPING
 * Users can log the Histories of each order, like: Phone calls, or mails, or offers.
 * Every order can have a company and a manager assigned from both the seller and buyer end.
 */
@Entity
@Table(name = "ORDER_TABLE")
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

    @Column //Should appear only when giving an offer (frontend)
    private Double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "order")//, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<History> histories;

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

    /**
     * Constructor for constructing Order object from DTO Object
     *  @param orderDTO The DTO to construct from.
     */
    public Order(OrderDTO orderDTO) {
        this.productName = orderDTO.getProductName();
        this.price = orderDTO.getPrice();
        this.status = orderDTO.getStatus();
        this.buyer = orderDTO.getBuyer();
        this.buyerManager = orderDTO.getBuyerManager();
        this.seller = orderDTO.getSeller();
        this.sellerManager = orderDTO.getSellerManager();
        if (!CollectionUtils.isEmpty(orderDTO.getHistory()))
            histories = orderDTO.getHistory().stream().map(History::new).collect(Collectors.toList());
    }
}
