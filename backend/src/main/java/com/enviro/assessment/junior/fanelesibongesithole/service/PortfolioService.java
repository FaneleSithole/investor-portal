package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.ActivityDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.AllocationDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ChartPointDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.PerformanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.PortfolioSummaryDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ProductDto;
import com.enviro.assessment.junior.fanelesibongesithole.model.Product;
import com.enviro.assessment.junior.fanelesibongesithole.repository.DataStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private final DataStore dataStore;

    public PortfolioService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public PortfolioSummaryDto getSummary() {
        List<Product> products = dataStore.getProducts();

        long totalValue = products.stream()
                .mapToLong(p -> p.getCurrentValue().longValue())
                .sum();

        List<ProductDto> productDtos = products.stream()
                .map(this::toProductDto)
                .toList();

        List<PerformanceDto> performance = products.stream()
                .map(p -> new PerformanceDto(
                        p.getName(),
                        p.getCommitted().longValue(),
                        p.getInvested().longValue(),
                        p.getIrr()))
                .toList();

        return new PortfolioSummaryDto(
                totalValue,
                12.4,
                products.size(),
                chartData(),
                allocations(products, totalValue),
                performance,
                recentActivity(),
                productDtos
        );
    }

    private ProductDto toProductDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getAssetClass(),
                p.getCommitted().longValue(),
                p.getInvested().longValue(),
                p.getCurrentValue().longValue(),
                p.getIrr()
        );
    }

    private List<ChartPointDto> chartData() {
        return List.of(
                new ChartPointDto("Jan", 210_000_000L),
                new ChartPointDto("Feb", 218_000_000L),
                new ChartPointDto("Mar", 222_000_000L),
                new ChartPointDto("Apr", 228_000_000L),
                new ChartPointDto("May", 231_000_000L),
                new ChartPointDto("Jun", 235_000_000L),
                new ChartPointDto("Jul", 238_000_000L),
                new ChartPointDto("Aug", 241_000_000L),
                new ChartPointDto("Sep", 244_000_000L),
                new ChartPointDto("Oct", 246_500_000L),
                new ChartPointDto("Nov", 247_800_000L),
                new ChartPointDto("Dec", 248_500_000L)
        );
    }

    private List<AllocationDto> allocations(List<Product> products, long totalValue) {
        Map<String, Long> byClass = new LinkedHashMap<>();
        for (Product p : products) {
            byClass.merge(p.getAssetClass(), p.getCurrentValue().longValue(), Long::sum);
        }
        return byClass.entrySet().stream()
                .map(e -> new AllocationDto(
                        e.getKey(),
                        totalValue == 0 ? 0
                                : BigDecimal.valueOf(e.getValue() * 100.0 / totalValue)
                                        .setScale(0, RoundingMode.HALF_UP).intValue()))
                .toList();
    }

    private List<ActivityDto> recentActivity() {
        return List.of(
                new ActivityDto("Capital Call", "Private Equity Funds", "2023-10-24", -2_500_000),
                new ActivityDto("Distribution", "Balanced / Hybrid Funds", "2023-10-18", 850_000),
                new ActivityDto("Document Ready", "Real Estate Investment Trusts (REITs)", "2023-10-15", 0)
        );
    }
}
