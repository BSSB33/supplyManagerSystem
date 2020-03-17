package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


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

    //TODO change it to ManyToMany
    @OneToOne(mappedBy = "director", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy = "buyerManager", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> buyerManager;

    @OneToMany(mappedBy = "sellerManager", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> sellerManager;

    @ManyToOne
    @JoinColumn
    private Company workplace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<History> histories;

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
