package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "linked_accounts")
public class LinkedAccountEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false, length = 4)
    private String lastFour;

    protected LinkedAccountEntity() {}

    public LinkedAccountEntity(String id, String bankName, String lastFour) {
        this.id = id;
        this.bankName = bankName;
        this.lastFour = lastFour;
    }

    public String getId() { return id; }
    public String getBankName() { return bankName; }
    public String getLastFour() { return lastFour; }
}
