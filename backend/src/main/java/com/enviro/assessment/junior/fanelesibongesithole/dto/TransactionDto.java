package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record TransactionDto(
        String referenceId,
        String date,
        double amount,
        String bankName,
        String lastFour,
        String status,
        String type,
        String reason
) {}
