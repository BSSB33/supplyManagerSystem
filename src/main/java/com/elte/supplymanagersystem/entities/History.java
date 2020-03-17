package com.elte.supplymanagersystem.entities;

import com.elte.supplymanagersystem.enums.HistoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Histories can be assigned to Orders
 * Every History has an Author so only those Users will see the History who works at the same company as the Author
 * With the help of HistoryType, Users can log the process of each order.
 */
@Entity
@Table(name = "HISTORY_TABLE")
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
    private User creator;

    @ManyToOne
    @JoinColumn
    //@JsonIgnore
    private Order order;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HistoryType historyType;

    @Column
    private String note;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
