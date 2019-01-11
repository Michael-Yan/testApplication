package com.zzz.monitor.bean;


public class LiveUrl {
    /**
     * rtmp : rtmp://stream.reiniot.com/live/92512960041109?auth_key=1497690356-0-0-7c06f8a919f39c2f4c7182d895238668
     * hls : http://stream.reiniot.com/live/92512960041109.m3u8?auth_key=1497690356-0-0-0a78477f1d04f169990972037d442ffa
     */

    private String rtmp;
    private String hls;

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }
}
