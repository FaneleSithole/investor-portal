package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_metrics")
public class PortfolioMetricsEntity {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private double growthPercent;

    protected PortfolioMetricsEntity() {}

    public PortfolioMetricsEntity(double growthPercent) {
        this.growthPercent = growthPercent;
    }

    public double getGrowthPercent() { return growthPercent; }
}
