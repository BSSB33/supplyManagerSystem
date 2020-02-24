package com.elte.supplymanagersystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="HISTORY_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HistoryType historyType;

    @Column
    private String note;

    //TODO add User, who added Timestamp
}
