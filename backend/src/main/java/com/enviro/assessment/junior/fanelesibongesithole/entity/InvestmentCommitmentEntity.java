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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_commitments")
public class InvestmentCommitmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fund_id", nullable = false)
    private FundEntity fund;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private LinkedAccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate fundingDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected InvestmentCommitmentEntity() {}

    public InvestmentCommitmentEntity(String referenceId, FundEntity fund, LinkedAccountEntity account,
                                      UserEntity user, BigDecimal amount, LocalDate fundingDate) {
        this.referenceId = referenceId;
        this.fund = fund;
        this.account = account;
        this.user = user;
        this.amount = amount;
        this.fundingDate = fundingDate;
        this.createdAt = LocalDateTime.now();
    }

    public String getReferenceId() { return referenceId; }
}
