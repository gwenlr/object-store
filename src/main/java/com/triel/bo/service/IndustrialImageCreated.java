package com.triel.bo.service;

class IndustrialImageCreated {

    private String objectName;
    private String imageUuid;

    public IndustrialImageCreated() {
    }

    public IndustrialImageCreated(String objectName, String imageUuid) {
        this.objectName = objectName;
        this.imageUuid = imageUuid;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndustrialImageCreated that = (IndustrialImageCreated) o;

        if (objectName != null ? !objectName.equals(that.objectName) : that.objectName != null) return false;
        return imageUuid != null ? imageUuid.equals(that.imageUuid) : that.imageUuid == null;
    }

    @Override
    public int hashCode() {
        int result = objectName != null ? objectName.hashCode() : 0;
        result = 31 * result + (imageUuid != null ? imageUuid.hashCode() : 0);
        return result;
    }
}