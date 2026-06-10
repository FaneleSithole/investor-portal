package com.enviro.assessment.junior.fanelesibongesithole.catalog;

import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentFundDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Single source of truth for investable funds — shared by portfolio holdings and new investments.
 */
@Component
public class FundCatalog {

    public static final long MIN_COMMITMENT = 4_500_000L;

    public record Fund(
            String id,
            String name,
            String description,
            String horizon,
            String assetClass
    ) {}

    private final Map<String, Fund> byId;

    public FundCatalog() {
        Map<String, Fund> catalog = new LinkedHashMap<>();
        catalog.put("fund_balanced", new Fund("fund_balanced", "Balanced / Hybrid Funds",
                "Diversified exposure across equities, bonds, and alternatives with moderate volatility.",
                "Medium-Long Term", "Balanced / Hybrid"));
        catalog.put("fund_target_date", new Fund("fund_target_date", "Target Date Funds",
                "Age-based allocation that automatically rebalances toward conservative assets over time.",
                "Medium-Long Term", "Target Date"));
        catalog.put("fund_etf", new Fund("fund_etf", "Exchange-Traded Funds (ETFs)",
                "Low-cost, liquid market exposure across sectors, indices, and asset classes.",
                "Medium-Long Term", "ETF"));
        catalog.put("fund_pe", new Fund("fund_pe", "Private Equity Funds",
                "Institutional access to buyout, growth, and venture strategies.",
                "Long Term", "Private Equity"));
        catalog.put("fund_mm", new Fund("fund_mm", "Money Market Funds",
                "Short-duration, high-liquidity instruments for capital preservation.",
                "Short Term", "Money Market"));
        catalog.put("fund_reit", new Fund("fund_reit", "Real Estate Investment Trusts (REITs)",
                "Income-focused commercial and residential property portfolios.",
                "Medium-Long Term", "Real Estate"));
        catalog.put("fund_esg", new Fund("fund_esg", "Sustainable & ESG Funds",
                "Impact-aligned strategies integrating environmental and governance criteria.",
                "Medium-Long Term", "ESG / Sustainable"));
        this.byId = Map.copyOf(catalog);
    }

    public List<Fund> all() {
        return List.copyOf(byId.values());
    }

    public Optional<Fund> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Fund require(String id) {
        Fund fund = byId.get(id);
        if (fund == null) {
            throw new IllegalArgumentException("Unknown fund: " + id);
        }
        return fund;
    }

    public InvestmentFundDto toDto(Fund fund) {
        return new InvestmentFundDto(
                fund.id(),
                fund.name(),
                fund.description(),
                fund.horizon(),
                MIN_COMMITMENT
        );
    }
}
