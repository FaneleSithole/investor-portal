package com.fanele.capitalflow.dto;

public record UserProfileDto(
        String firstName,
        String lastName,
        String firmName,
        String role,
        int age,
        boolean retirementEligible
) {}
