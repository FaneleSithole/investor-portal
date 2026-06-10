package com.enviro.assessment.junior.fanelesibongesithole.dto;

import com.enviro.assessment.junior.fanelesibongesithole.validation.ValidationPatterns;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentCommitmentRequest(
        @NotBlank @Pattern(regexp = ValidationPatterns.FUND_ID, message = "Invalid fund ID")
        String fundId,

        @NotNull @DecimalMin(value = "4500000.00", message = "Minimum commitment is R 4,500,000")
        BigDecimal amount,

        @NotBlank @Pattern(regexp = ValidationPatterns.ACCOUNT_ID, message = "Invalid account ID")
        String accountId,

        @NotNull @FutureOrPresent(message = "Funding date cannot be in the past")
        LocalDate fundingDate,

        @NotNull @AssertTrue(message = "Accredited investor confirmation is required")
        Boolean accreditedInvestor,

        @NotNull @AssertTrue(message = "Terms and conditions must be accepted")
        Boolean termsAccepted,

        @NotBlank @Size(min = 2, max = 120, message = "Digital signature is required")
        String digitalSignature
) {}
