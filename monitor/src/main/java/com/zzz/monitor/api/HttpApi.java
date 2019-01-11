package com.zzz.monitor.api;


import com.zzz.monitor.bean.DeviceListBean;
import com.zzz.monitor.bean.ListResponse;
import com.zzz.monitor.bean.LiveUrl;
import com.zzz.monitor.bean.Response;
import com.zzz.monitor.bean.Snapshot;
import com.zzz.monitor.bean.Token;
import com.zzz.monitor.bean.VodConfig;
import com.zzz.monitor.bean.VodUrl;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpApi {
    @FormUrlEncoded
    @POST("auth/token")
    Observable<Token> token(@Field("client_id") String clientId, @Field("client_secret") String clientSecret);

    @GET("api/mokan/getDeviceList")
    Observable<DeviceListBean> devices(@Query("page") int page, @Query("filter_is_online") int isOnline, @Query("filter_search") String keyword,@Query("who") int who);

    @GET("devices/{sn}/snapshots")
    Observable<ListResponse<Snapshot>> snapshots(@Path("sn") String sn, @Query("page") int page, @Query("start") String start, @Query("end") String end);

    @FormUrlEncoded
    @POST("devices/{sn}/ptz")
    Observable<Response> ptz(@Path("sn") String sn, @Field("direction") String direction);

    @FormUrlEncoded
    @POST("devices/{sn}/send-audio")
    Observable<Response> sendAudio(@Path("sn") String sn, @Field("audio") String audio);

    @POST("devices/{sn}/take-photo")
    Observable<Response> takePhoto(@Path("sn") String sn);

    @POST("devices/{sn}/live")
    Observable<LiveUrl> live(@Path("sn") String sn);

    @GET("devices/{sn}/vod-list")
    Observable<VodConfig> getVodSnippets(@Path("sn") String sn);

    @FormUrlEncoded
    @POST("devices/{sn}/play-vod")
    Observable<VodUrl> playVod(@Path("sn") String sn, @Field("unique_id") String uniqueId, @Field("timestamp") long timestamp);

    @FormUrlEncoded
    @POST("devices/{sn}/keep-play-vod")
    Observable<Response> keepVod(@Path("sn") String sn, @Field("unique_id") String uniqueId);
}
