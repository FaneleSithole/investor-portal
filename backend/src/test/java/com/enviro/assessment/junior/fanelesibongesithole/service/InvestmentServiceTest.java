package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.catalog.FundCatalog;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentResponse;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.FundRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.InvestmentCommitmentRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioHoldingRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReferenceCounterRepository;
import com.enviro.assessment.junior.fanelesibongesithole.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InvestmentServiceTest {

    @Mock
    private FundCatalog fundCatalog;
    @Mock
    private FundRepository fundRepository;
    @Mock
    private LinkedAccountRepository accountRepository;
    @Mock
    private PortfolioHoldingRepository holdingRepository;
    @Mock
    private InvestmentCommitmentRepository commitmentRepository;
    @Mock
    private ReferenceCounterRepository counterRepository;
    @Mock
    private CurrentUserService currentUserService;

    private InvestmentService investmentService;

    @BeforeEach
    void setUp() {
        when(currentUserService.requireCurrentUser()).thenReturn(
                new UserEntity("thabo@fanele.com", "hash", "Thabo", "Mokoena", null, LocalDate.of(1985, 6, 15))
        );
        when(fundCatalog.find("fund_pe")).thenReturn(Optional.of(peFund()));
        when(fundCatalog.find("fund_unknown")).thenReturn(Optional.empty());
        when(fundRepository.findById("fund_pe")).thenReturn(Optional.of(TestDataFactory.peFund()));
        when(accountRepository.findById("acc_001")).thenReturn(Optional.of(TestDataFactory.linkedAccount()));
        when(accountRepository.findById("acc_999")).thenReturn(Optional.empty());
        when(holdingRepository.findByFund_Id("fund_pe")).thenReturn(Optional.empty());
        when(holdingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(commitmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(counterRepository.findById(ReferenceCounterService.INVESTMENT_COUNTER))
                .thenReturn(Optional.of(TestDataFactory.investmentCounter()));

        ReferenceCounterService referenceCounterService = new ReferenceCounterService(counterRepository);
        investmentService = new InvestmentService(
                fundCatalog,
                fundRepository,
                accountRepository,
                holdingRepository,
                commitmentRepository,
                referenceCounterService,
                currentUserService
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

    private static FundCatalog.Fund peFund() {
        return new FundCatalog.Fund(
                "fund_pe",
                "Private Equity Funds",
                "Institutional access to buyout, growth, and venture strategies.",
                "Long Term",
                "Private Equity",
                4_500_000L
        );
    }
}
