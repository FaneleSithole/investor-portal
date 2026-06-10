package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record ComplianceSummaryDto(
        int passRate,
        int rulesPassed,
        int rulesWarning,
        int rulesFailed,
        boolean activeBreach,
        String breachMessage
) {}
