package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.ActivityDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.AllocationDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ChartPointDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.PerformanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.PortfolioSummaryDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ProductDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioHoldingEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioActivityRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioChartPointRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioHoldingRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioMetricsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private final PortfolioHoldingRepository holdingRepository;
    private final PortfolioChartPointRepository chartPointRepository;
    private final PortfolioActivityRepository activityRepository;
    private final PortfolioMetricsRepository metricsRepository;

    public PortfolioService(PortfolioHoldingRepository holdingRepository,
                            PortfolioChartPointRepository chartPointRepository,
                            PortfolioActivityRepository activityRepository,
                            PortfolioMetricsRepository metricsRepository) {
        this.holdingRepository = holdingRepository;
        this.chartPointRepository = chartPointRepository;
        this.activityRepository = activityRepository;
        this.metricsRepository = metricsRepository;
    }

    public PortfolioSummaryDto getSummary() {
        List<PortfolioHoldingEntity> holdings = holdingRepository.findAll();

        long totalValue = holdings.stream()
                .mapToLong(h -> h.getCurrentValue().longValue())
                .sum();

        List<ProductDto> productDtos = holdings.stream()
                .map(this::toProductDto)
                .toList();

        List<PerformanceDto> performance = holdings.stream()
                .map(h -> new PerformanceDto(
                        h.getFund().getName(),
                        h.getCommitted().longValue(),
                        h.getInvested().longValue(),
                        h.getIrr()))
                .toList();

        double growthPercent = metricsRepository.findById(1L)
                .map(m -> m.getGrowthPercent())
                .orElseThrow(() -> new ApiException("Portfolio metrics not configured", HttpStatus.INTERNAL_SERVER_ERROR));

        return new PortfolioSummaryDto(
                totalValue,
                growthPercent,
                holdings.size(),
                chartData(),
                allocations(holdings, totalValue),
                performance,
                recentActivity(),
                productDtos
        );
    }

    private ProductDto toProductDto(PortfolioHoldingEntity h) {
        return new ProductDto(
                h.getFund().getId(),
                h.getFund().getName(),
                h.getFund().getAssetClass(),
                h.getCommitted().longValue(),
                h.getInvested().longValue(),
                h.getCurrentValue().longValue(),
                h.getIrr()
        );
    }

    private List<ChartPointDto> chartData() {
        return chartPointRepository.findAllByOrderBySortOrderAsc().stream()
                .map(p -> new ChartPointDto(p.getMonthLabel(), p.getChartValue()))
                .toList();
    }

    private List<AllocationDto> allocations(List<PortfolioHoldingEntity> holdings, long totalValue) {
        Map<String, Long> byClass = new LinkedHashMap<>();
        for (PortfolioHoldingEntity h : holdings) {
            byClass.merge(h.getFund().getAssetClass(), h.getCurrentValue().longValue(), Long::sum);
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
        return activityRepository.findAllByOrderBySortOrderAsc().stream()
                .map(a -> new ActivityDto(
                        a.getActivityType(),
                        a.getFundName(),
                        a.getActivityDate().toString(),
                        a.getAmount()))
                .toList();
    }
}
