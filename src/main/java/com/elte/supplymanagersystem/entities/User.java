package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.dtos.UserDTO;
import com.elte.supplymanagersystem.enums.Role;
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
 * User Entity
 * Users are used for login and order management
 * A user have: Username, Password, Enabled/Disabled Status
 * A Company: If the User is the director of a Company
 * A User also can have connections with Orders, if the User is assigned to Orders
 * Workplace: Users are assigned to their workplace
 * Users are can be also creators of histories
 */
@Entity
@Table(name = "USER_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToOne
    @JoinColumn
    private Company company;

    //TODO rename fields
    //TODO hibernate logs off
    //TODO Optimise delete methods in services + commit + test run
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "buyerManager")
    @JsonIgnore
    private List<Order> buyerManager;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "sellerManager")
    @JsonIgnore
    private List<Order> sellerManager;

    @ManyToOne
    @JoinColumn
    private Company workplace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<History> histories;

    /**
     * Constructor for constructing User object from DTO Object
     *  @param userDTO The DTO to construct from.
     */
    public User(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.enabled = userDTO.isEnabled();
        this.company = userDTO.getCompany();
        this.workplace = userDTO.getWorkplace();
        this.role = userDTO.getRole();
        if (!CollectionUtils.isEmpty(userDTO.getHistories()))
            histories = userDTO.getHistories().stream().map(History::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userDTO.getBuyerManager()))
            buyerManager = userDTO.getBuyerManager().stream().map(Order::new).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userDTO.getSellerManager()))
            buyerManager = userDTO.getSellerManager().stream().map(Order::new).collect(Collectors.toList());
    }

    /**
     * Checks if the two users are colleagues or not
     *
     * @param otherUser possible colleague
     * @return true/false
     */
    public boolean isColleague(User otherUser) {
        return this.workplace.getId().equals(otherUser.workplace.getId());
    }
}
