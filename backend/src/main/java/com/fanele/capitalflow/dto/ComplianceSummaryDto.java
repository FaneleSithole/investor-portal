package com.fanele.capitalflow.dto;

public record ComplianceSummaryDto(
        int passRate,
        int rulesPassed,
        int rulesWarning,
        int rulesFailed,
        boolean activeBreach,
        String breachMessage
) {}
