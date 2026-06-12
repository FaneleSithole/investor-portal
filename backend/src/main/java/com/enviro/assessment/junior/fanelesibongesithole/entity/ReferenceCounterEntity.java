package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reference_counters")
public class ReferenceCounterEntity {

    @Id
    private String name;

    @Column(nullable = false)
    private int nextValue;

    protected ReferenceCounterEntity() {}

    public ReferenceCounterEntity(String name, int nextValue) {
        this.name = name;
        this.nextValue = nextValue;
    }

    public String getName() { return name; }
    public int getNextValue() { return nextValue; }
    public void setNextValue(int nextValue) { this.nextValue = nextValue; }
}
