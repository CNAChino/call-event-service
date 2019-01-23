package com.three55.callevent.model;

import com.three55.callevent.utils.NetworkType;

import java.time.LocalDate;

public class CellUsage {

    /**
     * Brand of the device, E.g. Samsung, Apple
     */
    private  String deviceBrand;

    /**
     * Model of the device, E.g. Galazy S7 (GS7), iphoneX
     */
    private  String deviceModel;

    /**
     * Signal Strength in decibel
     */
    private  int deviceSignalDb;

    /**
     * Date of the event
     */
    private LocalDate date;

    /**
     * Network Type used during event (e.g. 2G, 3G, LTE)
     */
    private NetworkType networkType;

    /**
     * Telco's name
     */
    private String networkName;

    /**
     * mobile network code
     */
    private String mnc;

    /**
     * mobile country code
     */
    private String mcc;

    // Getter and Setters

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getDeviceSignalDb() {
        return deviceSignalDb;
    }

    public void setDeviceSignalDb(int deviceSignalDb) {
        this.deviceSignalDb = deviceSignalDb;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
}
