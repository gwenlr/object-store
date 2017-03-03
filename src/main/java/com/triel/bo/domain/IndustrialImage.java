package com.triel.bo.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity(name="industrialimage")
public class IndustrialImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column
    private String contentType;

    @Column
    private byte[] content;

    @ManyToOne(optional = false)
    private IndustrialObject industrialObject;

    public IndustrialImage() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public IndustrialObject getIndustrialObject() {
        return industrialObject;
    }

    public void setIndustrialObject(IndustrialObject industrialObject) {
        this.industrialObject = industrialObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndustrialImage that = (IndustrialImage) o;

        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}