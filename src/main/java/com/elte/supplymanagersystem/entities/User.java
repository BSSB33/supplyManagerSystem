package com.elte.supplymanagersystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="USER_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne(mappedBy = "director", optional = true)
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy="buyerManager")
    @JsonIgnore
    private List<Order> buyerManager;

    @OneToMany(mappedBy="sellerManager")
    @JsonIgnore
    private List<Order> sellerManager;

    @ManyToOne
    @JoinColumn
    private Company workplace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * //Checks is the two users are colleagues or not
     * @param otherUser possible colleague
     * @return true/false
     */
    public boolean isColleague(User otherUser){
        return this.workplace.getId().equals(otherUser.workplace.getId());
    }
}
