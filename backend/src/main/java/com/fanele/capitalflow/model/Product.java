package com.fanele.capitalflow.model;

import java.math.BigDecimal;

public class Product {

    private final String id;
    private final String name;
    private final String assetClass;
    private final BigDecimal committed;
    private final BigDecimal invested;
    private final BigDecimal currentValue;
    private final double irr;

    public Product(String id, String name, String assetClass, BigDecimal committed,
                   BigDecimal invested, BigDecimal currentValue, double irr) {
        this.id = id;
        this.name = name;
        this.assetClass = assetClass;
        this.committed = committed;
        this.invested = invested;
        this.currentValue = currentValue;
        this.irr = irr;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAssetClass() { return assetClass; }
    public BigDecimal getCommitted() { return committed; }
    public BigDecimal getInvested() { return invested; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public double getIrr() { return irr; }
}
