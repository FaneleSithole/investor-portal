package com.enviro.assessment.junior.fanelesibongesithole.validation;

import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.LoginRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ProfileUpdateRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.RegisterRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.enviro.assessment.junior.fanelesibongesithole.validation.ValidationTestSupport.assertFieldInvalid;
import static com.enviro.assessment.junior.fanelesibongesithole.validation.ValidationTestSupport.assertValid;

class DtoValidationTest {

    @Test
    void registerRequest_acceptsValidPayload() {
        assertValid(new RegisterRequest(
                "investor@fanele.com",
                "password123",
                "Thabo",
                "Mokoena",
                "Fanele & Partners",
                LocalDate.of(1985, 6, 15)
        ));
    }

    @Test
    void registerRequest_rejectsShortPassword() {
        assertFieldInvalid(
                new RegisterRequest("investor@fanele.com", "short", "Thabo", "Mokoena", null, LocalDate.of(1985, 6, 15)),
                "password",
                "8 characters"
        );
    }

    @Test
    void registerRequest_rejectsInvalidFirstName() {
        assertFieldInvalid(
                new RegisterRequest("investor@fanele.com", "password123", "Thabo123", "Mokoena", null, LocalDate.of(1985, 6, 15)),
                "firstName",
                "Invalid first name"
        );
    }

    @Test
    void registerRequest_rejectsFutureDateOfBirth() {
        assertFieldInvalid(
                new RegisterRequest("investor@fanele.com", "password123", "Thabo", "Mokoena", null, LocalDate.now().plusDays(1)),
                "dateOfBirth",
                "past"
        );
    }

    @Test
    void loginRequest_acceptsValidPayload() {
        assertValid(new LoginRequest("thabo@fanele.com", "password123"));
    }

    @Test
    void loginRequest_rejectsBlankEmail() {
        assertFieldInvalid(new LoginRequest("", "password123"), "email", "must not be blank");
    }

    @Test
    void withdrawalRequest_acceptsValidPayload() {
        assertValid(new WithdrawalRequestDto(
                new BigDecimal("1000.00"),
                "acc_001",
                WithdrawalType.STANDARD,
                "Quarterly distribution"
        ));
    }

    @Test
    void withdrawalRequest_rejectsInvalidAccountId() {
        assertFieldInvalid(
                new WithdrawalRequestDto(new BigDecimal("1000.00"), "invalid", WithdrawalType.STANDARD, null),
                "accountId",
                "Invalid account ID"
        );
    }

    @Test
    void withdrawalRequest_rejectsZeroAmount() {
        assertFieldInvalid(
                new WithdrawalRequestDto(BigDecimal.ZERO, "acc_001", WithdrawalType.STANDARD, null),
                "amount",
                "greater than zero"
        );
    }

    @Test
    void investmentCommitment_acceptsValidPayload() {
        assertValid(new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("4500000.00"),
                "acc_001",
                LocalDate.now(),
                true,
                true,
                "Thabo Mokoena"
        ));
    }

    @Test
    void investmentCommitment_rejectsBelowMinimum() {
        assertFieldInvalid(
                new InvestmentCommitmentRequest(
                        "fund_pe",
                        new BigDecimal("1000.00"),
                        "acc_001",
                        LocalDate.now(),
                        true,
                        true,
                        "Thabo Mokoena"
                ),
                "amount",
                "Minimum commitment"
        );
    }

    @Test
    void investmentCommitment_rejectsPastFundingDate() {
        assertFieldInvalid(
                new InvestmentCommitmentRequest(
                        "fund_pe",
                        new BigDecimal("4500000.00"),
                        "acc_001",
                        LocalDate.now().minusDays(1),
                        true,
                        true,
                        "Thabo Mokoena"
                ),
                "fundingDate",
                "past"
        );
    }

    @Test
    void investmentCommitment_rejectsMissingAccreditation() {
        assertFieldInvalid(
                new InvestmentCommitmentRequest(
                        "fund_pe",
                        new BigDecimal("4500000.00"),
                        "acc_001",
                        LocalDate.now(),
                        false,
                        true,
                        "Thabo Mokoena"
                ),
                "accreditedInvestor",
                "Accredited investor"
        );
    }

    @Test
    void profileUpdate_acceptsValidPayload() {
        assertValid(new ProfileUpdateRequest(
                "Thabo",
                "Mokoena",
                "+27 82 555 1234",
                "Fanele & Partners",
                "Long-term allocator.",
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }

    @Test
    void profileUpdate_rejectsInvalidPhone() {
        assertFieldInvalid(
                new ProfileUpdateRequest("Thabo", "Mokoena", "not-a-phone", null, null, null, null, null, null, null, null),
                "phone",
                "Invalid phone number"
        );
    }

    @Test
    void profileUpdate_rejectsBioOverLimit() {
        assertFieldInvalid(
                new ProfileUpdateRequest("Thabo", "Mokoena", null, null, "x".repeat(2001), null, null, null, null, null, null),
                "bio",
                "2000"
        );
    }
}
