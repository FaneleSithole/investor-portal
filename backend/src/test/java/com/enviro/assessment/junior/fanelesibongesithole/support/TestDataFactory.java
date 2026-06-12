package com.enviro.assessment.junior.fanelesibongesithole.support;

import com.enviro.assessment.junior.fanelesibongesithole.entity.FundEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LinkedAccountEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LiquidBalanceEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.ReferenceCounterEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.WithdrawalEntity;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import com.enviro.assessment.junior.fanelesibongesithole.service.ReferenceCounterService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static FundEntity peFund() {
        return new FundEntity("fund_pe", "Private Equity Funds",
                "Institutional access to buyout, growth, and venture strategies.",
                "Long Term", "Private Equity", 4_500_000L, 4);
    }

    public static LinkedAccountEntity linkedAccount() {
        return new LinkedAccountEntity("acc_001", "First National Bank", "4592");
    }

    public static LiquidBalanceEntity liquidBalance() {
        return new LiquidBalanceEntity(new BigDecimal("2450000.00"), 2.4);
    }

    public static List<WithdrawalEntity> seedWithdrawals() {
        return List.of(
                new WithdrawalEntity("WDR-2024-001", LocalDate.of(2023, 10, 12),
                        new BigDecimal("150000.00"), "First National Bank", "4592",
                        WithdrawalStatus.Pending, WithdrawalType.STANDARD, "Q3 distribution"),
                new WithdrawalEntity("WDR-2024-002", LocalDate.of(2023, 9, 1),
                        new BigDecimal("500000.00"), "Standard Bank", "1108",
                        WithdrawalStatus.Completed, WithdrawalType.RETIREMENT, "Annual retirement draw")
        );
    }

    public static ReferenceCounterEntity withdrawalCounter() {
        return new ReferenceCounterEntity(ReferenceCounterService.WITHDRAWAL_COUNTER, 3);
    }

    public static ReferenceCounterEntity investmentCounter() {
        return new ReferenceCounterEntity(ReferenceCounterService.INVESTMENT_COUNTER, 101);
    }
}
