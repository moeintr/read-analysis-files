package org.example;

import java.io.Serializable;
import java.util.Map;

public class Organization implements Serializable {
    private OrganizationType organizationType;
    private boolean isEnable;
    private String companyCode;
    private Long account;
    private String name;


    public boolean isEnable() {
        return isEnable;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public Organization setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
        return this;
    }

    public Organization setEnable(boolean enable) {
        isEnable = enable;
        return this;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public Organization setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
        return this;
    }

    public Long getAccount() {
        return account;
    }

    public Organization setAccount(Long account) {
        this.account = account;
        return this;
    }

    public String getName() {
        return name;
    }

    public Organization setName(String name) {
        this.name = name;
        return this;
    }
}
