package org.example;

import java.io.Serializable;

public class Bill implements Serializable {
    private String billId;
    private Long amount;
    private Organization organization;

    public String getBillId() {
        return billId;
    }

    public Bill setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public Long getAmount() {
        return amount;
    }

    public Bill setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Bill setOrganization(Organization organization) {
        this.organization = organization;
        return this;
    }
}
