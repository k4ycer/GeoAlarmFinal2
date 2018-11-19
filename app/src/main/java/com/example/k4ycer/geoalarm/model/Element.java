package com.example.k4ycer.geoalarm.model;

public class Element {
    private String name;
    private String description;
    private Boolean status;

    public Element(String name, String description, Boolean status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
