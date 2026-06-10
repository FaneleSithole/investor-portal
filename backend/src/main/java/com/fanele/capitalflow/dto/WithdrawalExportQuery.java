package com.fanele.capitalflow.dto;

import com.fanele.capitalflow.validation.ValidationPatterns;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record WithdrawalExportQuery(
        @Pattern(regexp = ValidationPatterns.WITHDRAWAL_STATUS, message = "Invalid status filter")
        String status,

        @Pattern(regexp = ValidationPatterns.WITHDRAWAL_TYPE, message = "Invalid type filter")
        String type,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to
) {}
