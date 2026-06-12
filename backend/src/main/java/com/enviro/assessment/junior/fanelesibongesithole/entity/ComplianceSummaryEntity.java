package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compliance_summary")
public class ComplianceSummaryEntity {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private int passRate;

    @Column(nullable = false)
    private int rulesPassed;

    @Column(nullable = false)
    private int rulesWarning;

    @Column(nullable = false)
    private int rulesFailed;

    @Column(nullable = false)
    private boolean activeBreach;

    @Column(nullable = false, length = 1000)
    private String breachMessage;

    protected ComplianceSummaryEntity() {}

    public ComplianceSummaryEntity(int passRate, int rulesPassed, int rulesWarning, int rulesFailed,
                                   boolean activeBreach, String breachMessage) {
        this.passRate = passRate;
        this.rulesPassed = rulesPassed;
        this.rulesWarning = rulesWarning;
        this.rulesFailed = rulesFailed;
        this.activeBreach = activeBreach;
        this.breachMessage = breachMessage;
    }

    public Long getId() { return id; }
    public int getPassRate() { return passRate; }
    public int getRulesPassed() { return rulesPassed; }
    public int getRulesWarning() { return rulesWarning; }
    public int getRulesFailed() { return rulesFailed; }
    public boolean isActiveBreach() { return activeBreach; }
    public String getBreachMessage() { return breachMessage; }
}
