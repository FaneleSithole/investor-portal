package com.enviro.assessment.junior.fanelesibongesithole.entity;

import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false, length = 4)
    private String lastFour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalType type;

    @Column(nullable = false, length = 500)
    private String reason = "";

    protected WithdrawalEntity() {}

    public WithdrawalEntity(String referenceId, LocalDate date, BigDecimal amount,
                            String bankName, String lastFour, WithdrawalStatus status,
                            WithdrawalType type, String reason) {
        this.referenceId = referenceId;
        this.date = date;
        this.amount = amount;
        this.bankName = bankName;
        this.lastFour = lastFour;
        this.status = status;
        this.type = type;
        this.reason = reason != null ? reason : "";
    }

    public Long getId() { return id; }
    public String getReferenceId() { return referenceId; }
    public LocalDate getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public String getBankName() { return bankName; }
    public String getLastFour() { return lastFour; }
    public WithdrawalStatus getStatus() { return status; }
    public WithdrawalType getType() { return type; }
    public String getReason() { return reason; }
}
