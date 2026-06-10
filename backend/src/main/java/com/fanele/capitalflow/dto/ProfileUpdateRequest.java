package com.fanele.capitalflow.dto;

import com.fanele.capitalflow.validation.ValidationPatterns;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 80) @Pattern(regexp = ValidationPatterns.OPTIONAL_PERSON_NAME, message = "Invalid first name")
        String firstName,
        @Size(max = 80) @Pattern(regexp = ValidationPatterns.OPTIONAL_PERSON_NAME, message = "Invalid last name")
        String lastName,
        @Size(max = 30) @Pattern(regexp = ValidationPatterns.OPTIONAL_PHONE, message = "Invalid phone number")
        String phone,
        @Size(max = 120) String firmName,
        @Size(max = 2000) String bio,
        Boolean twoFactorEnabled,
        Boolean notifyPortfolio,
        Boolean notifyWithdrawals,
        Boolean notifyCompliance,
        Boolean notifyReports,
        Boolean notifyMarketing
) {}
