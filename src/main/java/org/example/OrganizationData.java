package org.example;

import java.io.Serializable;

public class OrganizationData implements Serializable {
    private int count;
    private long totalAmount;

    public OrganizationData incrementCount() {
        count++;
        return this;
    }

    public OrganizationData addAmount(long amount) {
        totalAmount += amount;
        return this;
    }

    public int getCount() {
        return count;
    }

    public long getTotalAmount() {
        return totalAmount;
    }
}
