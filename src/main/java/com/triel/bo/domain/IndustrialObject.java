package com.triel.bo.domain;


import javax.persistence.*;

@Entity(name = "industrialobject")
public class IndustrialObject {

    @Id
    private String name;

    @Column
    private String description;

    @Column
    private String state;


    public IndustrialObject() {
    }

    public IndustrialObject(String name, String description, String state) {
        this.name = name;
        this.description = description;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
