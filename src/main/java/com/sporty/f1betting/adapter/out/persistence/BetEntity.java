package com.sporty.f1betting.adapter.out.persistence;

import java.math.BigDecimal;

import com.sporty.f1betting.domain.model.BetStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "bets") 
@Data 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long sessionId;
    private Integer driverId;
    private BigDecimal amount;
    private Integer odds;
    @Enumerated(EnumType.STRING)
    private BetStatus status; // PENDING, WON, LOST
}
