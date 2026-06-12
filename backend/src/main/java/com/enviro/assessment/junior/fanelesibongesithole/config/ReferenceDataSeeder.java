package com.enviro.assessment.junior.fanelesibongesithole.config;

import com.enviro.assessment.junior.fanelesibongesithole.entity.ComplianceSummaryEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.FundEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LinkedAccountEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LiquidBalanceEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioActivityEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioChartPointEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioHoldingEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioMetricsEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.ReferenceCounterEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.ReportEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.WithdrawalEntity;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ComplianceSummaryRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.FundRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LiquidBalanceRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioActivityRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioChartPointRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioHoldingRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioMetricsRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReferenceCounterRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReportRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.WithdrawalRepository;
import com.enviro.assessment.junior.fanelesibongesithole.service.ReferenceCounterService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReferenceDataSeeder {

    private static final long MIN_COMMITMENT = 4_500_000L;

    private final FundRepository fundRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final LinkedAccountRepository accountRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final LiquidBalanceRepository liquidBalanceRepository;
    private final ReportRepository reportRepository;
    private final ComplianceSummaryRepository complianceSummaryRepository;
    private final PortfolioChartPointRepository chartPointRepository;
    private final PortfolioActivityRepository activityRepository;
    private final PortfolioMetricsRepository portfolioMetricsRepository;
    private final ReferenceCounterRepository counterRepository;

    public ReferenceDataSeeder(FundRepository fundRepository,
                                 PortfolioHoldingRepository holdingRepository,
                                 LinkedAccountRepository accountRepository,
                                 WithdrawalRepository withdrawalRepository,
                                 LiquidBalanceRepository liquidBalanceRepository,
                                 ReportRepository reportRepository,
                                 ComplianceSummaryRepository complianceSummaryRepository,
                                 PortfolioChartPointRepository chartPointRepository,
                                 PortfolioActivityRepository activityRepository,
                                 PortfolioMetricsRepository portfolioMetricsRepository,
                                 ReferenceCounterRepository counterRepository) {
        this.fundRepository = fundRepository;
        this.holdingRepository = holdingRepository;
        this.accountRepository = accountRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.liquidBalanceRepository = liquidBalanceRepository;
        this.reportRepository = reportRepository;
        this.complianceSummaryRepository = complianceSummaryRepository;
        this.chartPointRepository = chartPointRepository;
        this.activityRepository = activityRepository;
        this.portfolioMetricsRepository = portfolioMetricsRepository;
        this.counterRepository = counterRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void seedReferenceData() {
        if (fundRepository.count() > 0) {
            return;
        }
        seedFunds();
        seedAccounts();
        seedHoldings();
        seedWithdrawals();
        liquidBalanceRepository.save(new LiquidBalanceEntity(bd("2450000.00"), 2.4));
        seedReports();
        complianceSummaryRepository.save(new ComplianceSummaryEntity(
                94, 124, 3, 1, true,
                "Private Equity Funds has exceeded the maximum single-issuer exposure limit of 15%. " +
                        "Current exposure is 16.2% in TechHoldings LLC."
        ));
        seedChartPoints();
        seedActivities();
        portfolioMetricsRepository.save(new PortfolioMetricsEntity(12.4));
        counterRepository.save(new ReferenceCounterEntity(ReferenceCounterService.WITHDRAWAL_COUNTER, 3));
        counterRepository.save(new ReferenceCounterEntity(ReferenceCounterService.INVESTMENT_COUNTER, 101));
    }

    private void seedFunds() {
        fundRepository.saveAll(List.of(
                fund("fund_balanced", "Balanced / Hybrid Funds",
                        "Diversified exposure across equities, bonds, and alternatives with moderate volatility.",
                        "Medium-Long Term", "Balanced / Hybrid", 1),
                fund("fund_target_date", "Target Date Funds",
                        "Age-based allocation that automatically rebalances toward conservative assets over time.",
                        "Medium-Long Term", "Target Date", 2),
                fund("fund_etf", "Exchange-Traded Funds (ETFs)",
                        "Low-cost, liquid market exposure across sectors, indices, and asset classes.",
                        "Medium-Long Term", "ETF", 3),
                fund("fund_pe", "Private Equity Funds",
                        "Institutional access to buyout, growth, and venture strategies.",
                        "Long Term", "Private Equity", 4),
                fund("fund_mm", "Money Market Funds",
                        "Short-duration, high-liquidity instruments for capital preservation.",
                        "Short Term", "Money Market", 5),
                fund("fund_reit", "Real Estate Investment Trusts (REITs)",
                        "Income-focused commercial and residential property portfolios.",
                        "Medium-Long Term", "Real Estate", 6),
                fund("fund_esg", "Sustainable & ESG Funds",
                        "Impact-aligned strategies integrating environmental and governance criteria.",
                        "Medium-Long Term", "ESG / Sustainable", 7)
        ));
    }

    private void seedAccounts() {
        accountRepository.saveAll(List.of(
                new LinkedAccountEntity("acc_001", "First National Bank", "4592"),
                new LinkedAccountEntity("acc_002", "Standard Bank", "1108"),
                new LinkedAccountEntity("acc_003", "Discovery Bank", "8821")
        ));
    }

    private void seedHoldings() {
        Map<String, FundEntity> funds = fundRepository.findAll().stream()
                .collect(Collectors.toMap(FundEntity::getId, Function.identity()));

        holdingRepository.saveAll(List.of(
                holding(funds.get("fund_pe"), bd("65000000"), bd("58000000"), bd("62000000"), 14.8),
                holding(funds.get("fund_reit"), bd("52000000"), bd("52000000"), bd("48000000"), 8.1),
                holding(funds.get("fund_balanced"), bd("42000000"), bd("38000000"), bd("38000000"), 9.2),
                holding(funds.get("fund_etf"), bd("38000000"), bd("35000000"), bd("35000000"), 11.4),
                holding(funds.get("fund_esg"), bd("30000000"), bd("27000000"), bd("28000000"), 10.6),
                holding(funds.get("fund_target_date"), bd("24000000"), bd("22000000"), bd("22000000"), 7.8),
                holding(funds.get("fund_mm"), bd("18000000"), bd("18000000"), bd("15500000"), 5.2)
        ));
    }

    private void seedWithdrawals() {
        withdrawalRepository.saveAll(List.of(
                new WithdrawalEntity("WDR-2024-001", LocalDate.of(2023, 10, 12),
                        bd("150000.00"), "First National Bank", "4592",
                        WithdrawalStatus.Pending, WithdrawalType.STANDARD, "Q3 distribution"),
                new WithdrawalEntity("WDR-2024-002", LocalDate.of(2023, 9, 1),
                        bd("500000.00"), "Standard Bank", "1108",
                        WithdrawalStatus.Completed, WithdrawalType.RETIREMENT, "Annual retirement draw")
        ));
    }

    private void seedReports() {
        reportRepository.saveAll(List.of(
                new ReportEntity("rpt_001", "Q4 2023 Consolidated Statement",
                        "Comprehensive overview of portfolio performance ending Dec 31, 2023.",
                        "STATEMENT", LocalDate.of(2024, 1, 15), 2_516_582L),
                new ReportEntity("rpt_002", "2023 Schedule K-1",
                        "Partner's share of income, deductions, credits, etc. for tax year 2023.",
                        "TAX_DOC", LocalDate.of(2024, 3, 1), 1_153_434L),
                new ReportEntity("rpt_003", "Annual Performance Review",
                        "Deep dive into sector allocations and yield comparisons.",
                        "ANALYSIS", LocalDate.of(2024, 1, 20), 6_082_150L)
        ));
    }

    private void seedChartPoints() {
        chartPointRepository.saveAll(List.of(
                chartPoint("Jan", 210_000_000L, 1),
                chartPoint("Feb", 218_000_000L, 2),
                chartPoint("Mar", 222_000_000L, 3),
                chartPoint("Apr", 228_000_000L, 4),
                chartPoint("May", 231_000_000L, 5),
                chartPoint("Jun", 235_000_000L, 6),
                chartPoint("Jul", 238_000_000L, 7),
                chartPoint("Aug", 241_000_000L, 8),
                chartPoint("Sep", 244_000_000L, 9),
                chartPoint("Oct", 246_500_000L, 10),
                chartPoint("Nov", 247_800_000L, 11),
                chartPoint("Dec", 248_500_000L, 12)
        ));
    }

    private void seedActivities() {
        activityRepository.saveAll(List.of(
                new PortfolioActivityEntity("Capital Call", "Private Equity Funds",
                        LocalDate.of(2023, 10, 24), -2_500_000, 1),
                new PortfolioActivityEntity("Distribution", "Balanced / Hybrid Funds",
                        LocalDate.of(2023, 10, 18), 850_000, 2),
                new PortfolioActivityEntity("Document Ready", "Real Estate Investment Trusts (REITs)",
                        LocalDate.of(2023, 10, 15), 0, 3)
        ));
    }

    private static FundEntity fund(String id, String name, String description, String horizon,
                                   String assetClass, int sortOrder) {
        return new FundEntity(id, name, description, horizon, assetClass, MIN_COMMITMENT, sortOrder);
    }

    private static PortfolioHoldingEntity holding(FundEntity fund, BigDecimal committed, BigDecimal invested,
                                                  BigDecimal currentValue, double irr) {
        return new PortfolioHoldingEntity(fund, committed, invested, currentValue, irr);
    }

    private static PortfolioChartPointEntity chartPoint(String month, long value, int sortOrder) {
        return new PortfolioChartPointEntity(month, value, sortOrder);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
