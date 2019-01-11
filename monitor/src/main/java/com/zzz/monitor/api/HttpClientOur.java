package com.zzz.monitor.api;


import android.content.Context;

import com.zzz.monitor.HeadersPublicMonitor;
import com.zzz.monitor.PhoneSignMonitor;
import com.zzz.monitor.PreferencesMonitorUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClientOur {
    public static final String clientId = "";
    public static final String clientSecret = "";
    private static final String BASE_URL = "https://jzb.91zzz.net/app/";
//    private static final String BASE_URL = "http://192.168.1.100:8081/zzz-api/";
    private static final int DEFAULT_TIMEOUT = 300;
    public static String token = "";
    private final HttpApi mHttpApi;

    private HttpClientOur(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .header(HeadersPublicMonitor.IMEI, PhoneSignMonitor.getPhoneSign(context))
                                .header(HeadersPublicMonitor.USER_ID, PreferencesMonitorUtils.getString(context, HeadersPublicMonitor.USER_ID, ""))
                                .header(HeadersPublicMonitor.TOKEN, PreferencesMonitorUtils.getString(context, HeadersPublicMonitor.TOKEN, ""))
                                .header(HeadersPublicMonitor.SOURCE, HeadersPublicMonitor.SOURCE_VALUE)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mHttpApi = retrofit.create(HttpApi.class);
    }

    static Context mContext;

    public static HttpApi getInstance(Context context) {
        mContext=context;
        return SingletonHelper.INSTANCE.mHttpApi;
    }

    private static class SingletonHelper {
        private static final HttpClientOur INSTANCE = new HttpClientOur(mContext);
    }
}
