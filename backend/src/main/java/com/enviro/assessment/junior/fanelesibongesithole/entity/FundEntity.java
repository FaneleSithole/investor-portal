package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funds")
public class FundEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String horizon;

    @Column(nullable = false)
    private String assetClass;

    @Column(nullable = false)
    private long minCommitment;

    @Column(nullable = false)
    private int sortOrder;

    protected FundEntity() {}

    public FundEntity(String id, String name, String description, String horizon,
                      String assetClass, long minCommitment, int sortOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.horizon = horizon;
        this.assetClass = assetClass;
        this.minCommitment = minCommitment;
        this.sortOrder = sortOrder;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getHorizon() { return horizon; }
    public String getAssetClass() { return assetClass; }
    public long getMinCommitment() { return minCommitment; }
    public int getSortOrder() { return sortOrder; }
}
