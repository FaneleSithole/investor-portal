package com.fanele.capitalflow.dto;

public record InvestmentFundDto(
        String id,
        String name,
        String description,
        String horizon,
        long minCommitment
) {}
