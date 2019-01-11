package com.zzz.monitor.bean;

import com.google.gson.annotations.SerializedName;

public class Camera {

    /**
     * sn : 92515200948277
     * is_online : true
     * op : 中国电信
     * sim_one_network_type : 4G
     * sim_two_network_type : Unknown
     * sim_one_signal_level : 2
     * sim_two_signal_level : 0
     * address : 在戛纳湾·珍珠琇附近
     * latitude : 30.525395
     * longitude : 104.056955
     * power_type : out
     * snapshot : http://snapshots.reiniot.com/92515200948277/1511834529.webp
     * online_at : 2017-11-28 02:27:08
     */

    private String sn;
    @SerializedName("is_online")
    private boolean isOnline;
    private String op;
    @SerializedName("sim_one_network_type")
    private String simOneNetworkType;
    @SerializedName("sim_two_network_type")
    private String simTwoNetworkType;
    @SerializedName("sim_one_signal_level")
    private int simOneSignalLevel;
    @SerializedName("sim_two_signal_level")
    private int simTwoSignalLevel;
    private String address;
    private String latitude;
    private String longitude;
    @SerializedName("power_type")
    private String powerType;
    private String snapshot;
    @SerializedName("online_at")
    private String onlineAt;
    private String name="";

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getSimOneNetworkType() {
        return simOneNetworkType;
    }

    public void setSimOneNetworkType(String simOneNetworkType) {
        this.simOneNetworkType = simOneNetworkType;
    }

    public String getSimTwoNetworkType() {
        return simTwoNetworkType;
    }

    public void setSimTwoNetworkType(String simTwoNetworkType) {
        this.simTwoNetworkType = simTwoNetworkType;
    }

    public int getSimOneSignalLevel() {
        return simOneSignalLevel;
    }

    public void setSimOneSignalLevel(int simOneSignalLevel) {
        this.simOneSignalLevel = simOneSignalLevel;
    }

    public int getSimTwoSignalLevel() {
        return simTwoSignalLevel;
    }

    public void setSimTwoSignalLevel(int simTwoSignalLevel) {
        this.simTwoSignalLevel = simTwoSignalLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPowerType() {
        return powerType;
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getOnlineAt() {
        return onlineAt;
    }

    public void setOnlineAt(String onlineAt) {
        this.onlineAt = onlineAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
