package com.fanele.capitalflow.model;

public class LinkedAccount {

    private final String id;
    private final String bankName;
    private final String lastFour;

    public LinkedAccount(String id, String bankName, String lastFour) {
        this.id = id;
        this.bankName = bankName;
        this.lastFour = lastFour;
    }

    public String getId() { return id; }
    public String getBankName() { return bankName; }
    public String getLastFour() { return lastFour; }
}
