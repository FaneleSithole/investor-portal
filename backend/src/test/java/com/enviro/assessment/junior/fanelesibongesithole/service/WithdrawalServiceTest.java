package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.catalog.FundCatalog;
import com.enviro.assessment.junior.fanelesibongesithole.dto.BalanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    private static final BigDecimal AVAILABLE = new BigDecimal("2300000.00");
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("2070000.00");

    @Mock
    private CurrentUserService currentUserService;

    private DataStore dataStore;
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        dataStore = new DataStore(new FundCatalog());
        withdrawalService = new WithdrawalService(dataStore, currentUserService);
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
