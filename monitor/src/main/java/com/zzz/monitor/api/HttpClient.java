package com.zzz.monitor.api;


import com.zzz.monitor.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {
    public static final String clientId = "";
    public static final String clientSecret = "";
    private static final String BASE_URL = "http://api.reiniot.com/v1/";
    private static final int DEFAULT_TIMEOUT = 300;
    public static String token = "";
    private final HttpApi mHttpApi;

    private HttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("User-Agent", "Mokan/Android/Client/" + BuildConfig.VERSION_NAME)
                                .header("Authorization", token)
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

    public static HttpApi getInstance() {
        return SingletonHelper.INSTANCE.mHttpApi;
    }

    private static class SingletonHelper {
        private static final HttpClient INSTANCE = new HttpClient();
    }
}
