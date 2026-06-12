package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_chart_points")
public class PortfolioChartPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String monthLabel;

    @Column(name = "chart_value", nullable = false)
    private long chartValue;

    @Column(nullable = false)
    private int sortOrder;

    protected PortfolioChartPointEntity() {}

    public PortfolioChartPointEntity(String monthLabel, long chartValue, int sortOrder) {
        this.monthLabel = monthLabel;
        this.chartValue = chartValue;
        this.sortOrder = sortOrder;
    }

    public String getMonthLabel() { return monthLabel; }
    public long getChartValue() { return chartValue; }
    public int getSortOrder() { return sortOrder; }
}
