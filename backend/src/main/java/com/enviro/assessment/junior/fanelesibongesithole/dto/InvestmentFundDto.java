package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record InvestmentFundDto(
        String id,
        String name,
        String description,
        String horizon,
        long minCommitment
) {}
