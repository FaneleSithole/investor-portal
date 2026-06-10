package com.enviro.assessment.junior.fanelesibongesithole.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Withdrawal {

    private final String referenceId;
    private final LocalDate date;
    private final BigDecimal amount;
    private final String bankName;
    private final String lastFour;
    private final WithdrawalStatus status;
    private final WithdrawalType type;
    private final String reason;

    public Withdrawal(String referenceId, LocalDate date, BigDecimal amount,
                      String bankName, String lastFour, WithdrawalStatus status,
                      WithdrawalType type, String reason) {
        this.referenceId = referenceId;
        this.date = date;
        this.amount = amount;
        this.bankName = bankName;
        this.lastFour = lastFour;
        this.status = status;
        this.type = type;
        this.reason = reason;
    }

    public String getReferenceId() { return referenceId; }
    public LocalDate getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public String getBankName() { return bankName; }
    public String getLastFour() { return lastFour; }
    public WithdrawalStatus getStatus() { return status; }
    public WithdrawalType getType() { return type; }
    public String getReason() { return reason; }
}
