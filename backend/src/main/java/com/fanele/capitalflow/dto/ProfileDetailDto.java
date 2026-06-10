package com.fanele.capitalflow.dto;

public record ProfileDetailDto(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String firmName,
        String role,
        String bio,
        int age,
        boolean retirementEligible,
        boolean verified,
        int joinedYear,
        int securityProgress,
        boolean twoFactorEnabled,
        boolean notifyPortfolio,
        boolean notifyWithdrawals,
        boolean notifyCompliance,
        boolean notifyReports,
        boolean notifyMarketing
) {}
