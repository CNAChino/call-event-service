package com.three55.callevent.model;

public class CellAverageUsage {

    /**
     * device brand
     */
    private String brand;

    /**
     * device model
     */
    private String model;

    /**
     * usage count of this device brand/model
     */
    private int usageCount;

    /**
     * average signal strent of this device model
     */
    private float avgSignalStrength;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public float getAvgSignalStrength() {
        return avgSignalStrength;
    }

    public void setAvgSignalStrength(float avgSignalStrength) {
        this.avgSignalStrength = avgSignalStrength;
    }
}
