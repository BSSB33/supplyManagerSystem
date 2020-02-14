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
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private historyType historyType;

    @Column
    private String note;

    public enum historyType{
        PHONE_CALL, EMAIL_SENT, MADE_AND_OFFER, INTERESTED
    }
}
