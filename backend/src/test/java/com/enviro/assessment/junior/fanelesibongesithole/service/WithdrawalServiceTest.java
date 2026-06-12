package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.BalanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LiquidBalanceRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReferenceCounterRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.WithdrawalRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WithdrawalServiceTest {

    private static final BigDecimal AVAILABLE = new BigDecimal("2300000.00");
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("2070000.00");

    @Mock
    private WithdrawalRepository withdrawalRepository;
    @Mock
    private LinkedAccountRepository accountRepository;
    @Mock
    private LiquidBalanceRepository liquidBalanceRepository;
    @Mock
    private ReferenceCounterRepository counterRepository;
    @Mock
    private CurrentUserService currentUserService;

    private final List<com.enviro.assessment.junior.fanelesibongesithole.entity.WithdrawalEntity> withdrawals =
            new ArrayList<>();
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        withdrawals.clear();
        withdrawals.addAll(TestDataFactory.seedWithdrawals());

        when(withdrawalRepository.findAll()).thenAnswer(invocation -> List.copyOf(withdrawals));
        when(withdrawalRepository.save(any())).thenAnswer(invocation -> {
            com.enviro.assessment.junior.fanelesibongesithole.entity.WithdrawalEntity saved =
                    invocation.getArgument(0);
            withdrawals.add(0, saved);
            return saved;
        });
        when(liquidBalanceRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.liquidBalance()));
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById("acc_001")).thenReturn(Optional.of(TestDataFactory.linkedAccount()));
        when(counterRepository.findById(ReferenceCounterService.WITHDRAWAL_COUNTER))
                .thenReturn(Optional.of(TestDataFactory.withdrawalCounter()));

        ReferenceCounterService referenceCounterService = new ReferenceCounterService(counterRepository);
        withdrawalService = new WithdrawalService(
                withdrawalRepository,
                accountRepository,
                liquidBalanceRepository,
                referenceCounterService,
                currentUserService
        );
    }

    @Test
    void getBalance_reflectsPendingWithdrawalsAndRetirementEligibility() {
        when(currentUserService.requireCurrentUser()).thenReturn(retiree());

        BalanceDto balance = withdrawalService.getBalance();

        assertEquals(AVAILABLE.doubleValue(), balance.availableBalance(), 0.01);
        assertEquals(MAX_WITHDRAWAL.doubleValue(), balance.maxWithdrawalAmount(), 0.01);
        assertTrue(balance.retirementEligible());
    }

    @Test
    void getBalance_marksYoungInvestorAsNotRetirementEligible() {
        when(currentUserService.requireCurrentUser()).thenReturn(youngInvestor());

        BalanceDto balance = withdrawalService.getBalance();

        assertFalse(balance.retirementEligible());
    }

    @Test
    void createWithdrawal_succeedsForValidStandardRequest() {
        when(currentUserService.requireCurrentUser()).thenReturn(youngInvestor());

        WithdrawalResponseDto response = withdrawalService.createWithdrawal(
                new WithdrawalRequestDto(new BigDecimal("50000.00"), "acc_001", WithdrawalType.STANDARD, "Test"));

        assertNotNull(response.referenceId());
        assertEquals("Withdrawal request submitted", response.message());
    }

    @Test
    void createWithdrawal_rejectsAmountAboveAvailableBalance() {
        when(currentUserService.requireCurrentUser()).thenReturn(youngInvestor());

        ApiException ex = assertThrows(ApiException.class, () -> withdrawalService.createWithdrawal(
                new WithdrawalRequestDto(new BigDecimal("2400000.00"), "acc_001", WithdrawalType.STANDARD, null)));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("available balance"));
    }

    @Test
    void createWithdrawal_rejectsAmountAboveNinetyPercentCap() {
        when(currentUserService.requireCurrentUser()).thenReturn(youngInvestor());

        ApiException ex = assertThrows(ApiException.class, () -> withdrawalService.createWithdrawal(
                new WithdrawalRequestDto(new BigDecimal("2100000.00"), "acc_001", WithdrawalType.STANDARD, null)));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("90%"));
    }

    @Test
    void createWithdrawal_rejectsRetirementWithdrawalForYoungInvestor() {
        when(currentUserService.requireCurrentUser()).thenReturn(youngInvestor());

        ApiException ex = assertThrows(ApiException.class, () -> withdrawalService.createWithdrawal(
                new WithdrawalRequestDto(new BigDecimal("1000.00"), "acc_001", WithdrawalType.RETIREMENT, null)));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        assertTrue(ex.getMessage().contains("age 65"));
    }

    @Test
    void createWithdrawal_rejectsUnknownAccount() {
        ApiException ex = assertThrows(ApiException.class, () -> withdrawalService.createWithdrawal(
                new WithdrawalRequestDto(new BigDecimal("1000.00"), "acc_999", WithdrawalType.STANDARD, null)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Invalid destination account", ex.getMessage());
    }

    private static UserEntity retiree() {
        return new UserEntity("retiree@fanele.com", "hash", "Thabo", "Mokoena", null, LocalDate.of(1950, 1, 1));
    }

    private static UserEntity youngInvestor() {
        return new UserEntity("young@fanele.com", "hash", "Lerato", "Ndlovu", null, LocalDate.of(1990, 3, 20));
    }
}
