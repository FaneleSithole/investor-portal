package com.enviro.assessment.junior.fanelesibongesithole.dto;

import java.util.List;

public record PortfolioSummaryDto(
        long totalValue,
        double growthPercent,
        int fundCount,
        List<ChartPointDto> chartData,
        List<AllocationDto> allocations,
        List<PerformanceDto> performanceByClass,
        List<ActivityDto> recentActivity,
        List<ProductDto> products
) {}
