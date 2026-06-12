package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "portfolio_activities")
public class PortfolioActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String activityType;

    @Column(nullable = false)
    private String fundName;

    @Column(nullable = false)
    private LocalDate activityDate;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private int sortOrder;

    protected PortfolioActivityEntity() {}

    public PortfolioActivityEntity(String activityType, String fundName, LocalDate activityDate,
                                   long amount, int sortOrder) {
        this.activityType = activityType;
        this.fundName = fundName;
        this.activityDate = activityDate;
        this.amount = amount;
        this.sortOrder = sortOrder;
    }

    public String getActivityType() { return activityType; }
    public String getFundName() { return fundName; }
    public LocalDate getActivityDate() { return activityDate; }
    public long getAmount() { return amount; }
    public int getSortOrder() { return sortOrder; }
}
