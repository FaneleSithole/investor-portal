package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.catalog.FundCatalog;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentResponse;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    private DataStore dataStore;
    private InvestmentService investmentService;

    @BeforeEach
    void setUp() {
        FundCatalog fundCatalog = new FundCatalog();
        dataStore = new DataStore(fundCatalog);
        investmentService = new InvestmentService(fundCatalog, dataStore, currentUserService);
        when(currentUserService.requireCurrentUser()).thenReturn(
                new UserEntity("thabo@fanele.com", "hash", "Thabo", "Mokoena", null, LocalDate.of(1985, 6, 15))
        );
    }

    @Test
    void createCommitment_succeedsForValidRequest() {
        InvestmentCommitmentResponse response = investmentService.createCommitment(validRequest());

        assertNotNull(response.referenceId());
        assertTrue(response.message().contains("Private Equity Funds"));
    }

    @Test
    void createCommitment_rejectsBelowMinimumAmount() {
        InvestmentCommitmentRequest request = new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("1000000.00"),
                "acc_001",
                LocalDate.now(),
                true,
                true,
                "Thabo Mokoena"
        );

        ApiException ex = assertThrows(ApiException.class, () -> investmentService.createCommitment(request));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("Minimum commitment"));
    }

    @Test
    void createCommitment_rejectsMismatchedDigitalSignature() {
        InvestmentCommitmentRequest request = new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("4500000.00"),
                "acc_001",
                LocalDate.now(),
                true,
                true,
                "Wrong Name"
        );

        ApiException ex = assertThrows(ApiException.class, () -> investmentService.createCommitment(request));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("Digital signature"));
    }

    @Test
    void createCommitment_rejectsMissingAccreditation() {
        InvestmentCommitmentRequest request = new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("4500000.00"),
                "acc_001",
                LocalDate.now(),
                false,
                true,
                "Thabo Mokoena"
        );

        ApiException ex = assertThrows(ApiException.class, () -> investmentService.createCommitment(request));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("Accredited investor"));
    }

    @Test
    void createCommitment_rejectsUnknownFund() {
        InvestmentCommitmentRequest request = new InvestmentCommitmentRequest(
                "fund_unknown",
                new BigDecimal("4500000.00"),
                "acc_001",
                LocalDate.now(),
                true,
                true,
                "Thabo Mokoena"
        );

        ApiException ex = assertThrows(ApiException.class, () -> investmentService.createCommitment(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Fund not found", ex.getMessage());
    }

    @Test
    void createCommitment_rejectsInvalidFundingAccount() {
        InvestmentCommitmentRequest request = new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("4500000.00"),
                "acc_999",
                LocalDate.now(),
                true,
                true,
                "Thabo Mokoena"
        );

        ApiException ex = assertThrows(ApiException.class, () -> investmentService.createCommitment(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Invalid funding source account", ex.getMessage());
    }

    private static InvestmentCommitmentRequest validRequest() {
        return new InvestmentCommitmentRequest(
                "fund_pe",
                new BigDecimal("4500000.00"),
                "acc_001",
                LocalDate.now(),
                true,
                true,
                "Thabo Mokoena"
        );
    }
}
