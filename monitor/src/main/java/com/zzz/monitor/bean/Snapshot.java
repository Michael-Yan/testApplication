package com.zzz.monitor.bean;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Snapshot {
    /**
     * created_at : 2017-06-01 16:42:15
     * original : http://snapshots.reiniot.com/92512069285153/1496306535.webp?x-oss-process=image/resize,h_720,w_1280,m_fixed,limit_0/watermark,image_bG9nb18wMS5wbmc_eC1vc3MtcHJvY2Vzcz1pbWFnZS9yZXNpemUsUF8yMA==,t_100,g_nw,x_20,y_20/watermark,type_d3F5LXplbmhlaQ,size_65,text_MjAxNy0wNi0wMSAxNjo0MjoxNQ==,color_CCCCCC,t_95,g_south,y_30/sharpen,0/quality,Q_65/format,jpg
     * small : http://snapshots.reiniot.com/92512069285153/1496306535.webp?x-oss-process=image/resize,h_72,w_128,m_fixed,limit_0/watermark,image_bG9nb18wMS5wbmc_eC1vc3MtcHJvY2Vzcz1pbWFnZS9yZXNpemUsUF8yMA==,t_100,g_nw,x_2,y_2/watermark,type_d3F5LXplbmhlaQ,size_7,text_MjAxNy0wNi0wMSAxNjo0MjoxNQ==,color_CCCCCC,t_95,g_south,y_3/sharpen,0/quality,Q_65/format,jpg
     */

    @SerializedName("created_at")
    private String createdAt;
    private String original;
    private String small;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getSdfDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(this.createdAt);
            sdf = new SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINA);
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
