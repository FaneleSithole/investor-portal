package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record BalanceDto(
        double availableBalance,
        double maxWithdrawalAmount,
        double growthPercent,
        boolean retirementEligible
) {}
