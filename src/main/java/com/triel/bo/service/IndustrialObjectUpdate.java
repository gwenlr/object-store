package com.triel.bo.service;

public class IndustrialObjectUpdate {

    private String description;

    private String state;


    public IndustrialObjectUpdate() {
    }

    public IndustrialObjectUpdate(String description, String state) {
        this.description = description;
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndustrialObjectUpdate that = (IndustrialObjectUpdate) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return state != null ? state.equals(that.state) : that.state == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
