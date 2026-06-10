package com.fanele.capitalflow.dto;

public record BalanceDto(
        double availableBalance,
        double maxWithdrawalAmount,
        double growthPercent,
        boolean retirementEligible
) {}
