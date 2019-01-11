package com.zzz.monitor.bean;

import com.google.gson.annotations.SerializedName;


public class VodUrl {
    /**
     * default : rtmp://stream.reiniot.com/playback/92518330792854?auth_key=1497001780-0-0-693f586ab14f1b56924e5cbd7c291b51
     */

    @SerializedName("default")
    private String defaultX;

    public String getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(String defaultX) {
        this.defaultX = defaultX;
    }
}
