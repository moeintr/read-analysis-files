package org.example;

public class OrganizationType {
    private String name;
    private Integer organizationType;

    public String getName() {
        return name;
    }

    public OrganizationType setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public OrganizationType setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
        return this;
    }
}
