package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.catalog.FundCatalog;
import com.enviro.assessment.junior.fanelesibongesithole.model.LinkedAccount;
import com.enviro.assessment.junior.fanelesibongesithole.model.Product;
import com.enviro.assessment.junior.fanelesibongesithole.model.Withdrawal;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DataStore {

    private static final BigDecimal LIQUID_BALANCE = new BigDecimal("2450000.00");

    private final List<Product> products;
    private final Map<String, LinkedAccount> accounts;
    private final List<Withdrawal> withdrawals = new ArrayList<>();
    private final AtomicInteger withdrawalCounter = new AtomicInteger(2);

    public DataStore(FundCatalog catalog) {
        products = new ArrayList<>(List.of(
                holding(catalog.require("fund_pe"),
                        bd("65000000"), bd("58000000"), bd("62000000"), 14.8),
                holding(catalog.require("fund_reit"),
                        bd("52000000"), bd("52000000"), bd("48000000"), 8.1),
                holding(catalog.require("fund_balanced"),
                        bd("42000000"), bd("38000000"), bd("38000000"), 9.2),
                holding(catalog.require("fund_etf"),
                        bd("38000000"), bd("35000000"), bd("35000000"), 11.4),
                holding(catalog.require("fund_esg"),
                        bd("30000000"), bd("27000000"), bd("28000000"), 10.6),
                holding(catalog.require("fund_target_date"),
                        bd("24000000"), bd("22000000"), bd("22000000"), 7.8),
                holding(catalog.require("fund_mm"),
                        bd("18000000"), bd("18000000"), bd("15500000"), 5.2)
        ));

        accounts = new ConcurrentHashMap<>();
        accounts.put("acc_001", new LinkedAccount("acc_001", "First National Bank", "4592"));
        accounts.put("acc_002", new LinkedAccount("acc_002", "Standard Bank", "1108"));
        accounts.put("acc_003", new LinkedAccount("acc_003", "Discovery Bank", "8821"));

        withdrawals.add(new Withdrawal("WDR-2024-001", LocalDate.of(2023, 10, 12),
                new BigDecimal("150000.00"), "First National Bank", "4592",
                WithdrawalStatus.Pending, WithdrawalType.STANDARD, "Q3 distribution"));
        withdrawals.add(new Withdrawal("WDR-2024-002", LocalDate.of(2023, 9, 1),
                new BigDecimal("500000.00"), "Standard Bank", "1108",
                WithdrawalStatus.Completed, WithdrawalType.RETIREMENT, "Annual retirement draw"));
    }

    private static Product holding(FundCatalog.Fund fund, BigDecimal committed, BigDecimal invested,
                                   BigDecimal currentValue, double irr) {
        return new Product(fund.id(), fund.name(), fund.assetClass(), committed, invested, currentValue, irr);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    public List<Product> getProducts() {
        return products;
    }

    public Optional<Product> findProductByFundId(String fundId) {
        return products.stream().filter(p -> p.getId().equals(fundId)).findFirst();
    }

    public void recordCommitment(String fundId, BigDecimal amount, FundCatalog.Fund fund) {
        Optional<Product> existing = findProductByFundId(fundId);
        if (existing.isPresent()) {
            Product old = existing.get();
            int index = products.indexOf(old);
            products.set(index, new Product(
                    old.getId(),
                    old.getName(),
                    old.getAssetClass(),
                    old.getCommitted().add(amount),
                    old.getInvested(),
                    old.getCurrentValue().add(amount),
                    old.getIrr()
            ));
        } else {
            products.add(new Product(
                    fund.id(),
                    fund.name(),
                    fund.assetClass(),
                    amount,
                    BigDecimal.ZERO,
                    amount,
                    0.0
            ));
        }
    }

    public BigDecimal getLiquidBalance() {
        return LIQUID_BALANCE;
    }

    public Map<String, LinkedAccount> getAccounts() {
        return accounts;
    }

    public List<Withdrawal> getWithdrawals() {
        return withdrawals;
    }

    public void addWithdrawal(Withdrawal withdrawal) {
        withdrawals.add(0, withdrawal);
    }

    public String nextReferenceId() {
        return String.format("WDR-2024-%03d", withdrawalCounter.incrementAndGet());
    }
}
