package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "liquid_balances")
public class LiquidBalanceEntity {

    @Id
    private Long id = 1L;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private double growthPercent;

    protected LiquidBalanceEntity() {}

    public LiquidBalanceEntity(BigDecimal balance, double growthPercent) {
        this.balance = balance;
        this.growthPercent = growthPercent;
    }

    public Long getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public double getGrowthPercent() { return growthPercent; }
}
