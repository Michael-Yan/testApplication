package com.zzz.monitor.bean;

import com.google.gson.annotations.SerializedName;


public class Token {
    /**
     * token_type : bearer
     * access_token : JNHrGOnsOcaEerTh0LmYSgGFfhzOBPLH
     * expires_in : 604800
     */

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private int expiresIn;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAuthorization() {
        return tokenType + " " + accessToken;
    }
}
