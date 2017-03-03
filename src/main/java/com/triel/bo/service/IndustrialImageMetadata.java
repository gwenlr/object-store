package com.triel.bo.service;

class IndustrialImageMetadata {

    private String uuid;
    private String contentType;

    public IndustrialImageMetadata() {
    }

    public IndustrialImageMetadata(String uuid, String contentType) {
        this.uuid = uuid;
        this.contentType = contentType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndustrialImageMetadata that = (IndustrialImageMetadata) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        return contentType != null ? contentType.equals(that.contentType) : that.contentType == null;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }
}