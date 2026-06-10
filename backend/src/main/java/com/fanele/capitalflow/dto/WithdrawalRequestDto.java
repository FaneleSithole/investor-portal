package com.fanele.capitalflow.dto;

import com.fanele.capitalflow.model.WithdrawalType;
import com.fanele.capitalflow.validation.ValidationPatterns;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WithdrawalRequestDto(
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Account is required")
        @Pattern(regexp = ValidationPatterns.ACCOUNT_ID, message = "Invalid account ID")
        String accountId,

        @NotNull(message = "Withdrawal type is required")
        WithdrawalType type,

        @Size(max = 500, message = "Reason must be at most 500 characters")
        String reason
) {}
