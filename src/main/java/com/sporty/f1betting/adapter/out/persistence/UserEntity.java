package com.sporty.f1betting.adapter.out.persistence;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "users") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id private Long id;
    private BigDecimal balance;
}
