package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record UserProfileDto(
        String firstName,
        String lastName,
        String firmName,
        String role,
        int age,
        boolean retirementEligible
) {}
