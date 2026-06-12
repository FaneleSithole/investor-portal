package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_holdings")
public class PortfolioHoldingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fund_id", nullable = false)
    private FundEntity fund;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal committed;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal invested;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentValue;

    @Column(nullable = false)
    private double irr;

    protected PortfolioHoldingEntity() {}

    public PortfolioHoldingEntity(FundEntity fund, BigDecimal committed, BigDecimal invested,
                                  BigDecimal currentValue, double irr) {
        this.fund = fund;
        this.committed = committed;
        this.invested = invested;
        this.currentValue = currentValue;
        this.irr = irr;
    }

    public Long getId() { return id; }
    public FundEntity getFund() { return fund; }
    public BigDecimal getCommitted() { return committed; }
    public BigDecimal getInvested() { return invested; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public double getIrr() { return irr; }

    public void setCommitted(BigDecimal committed) { this.committed = committed; }
    public void setInvested(BigDecimal invested) { this.invested = invested; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    public void setIrr(double irr) { this.irr = irr; }
}
