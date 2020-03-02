package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToOne(mappedBy = "director", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
