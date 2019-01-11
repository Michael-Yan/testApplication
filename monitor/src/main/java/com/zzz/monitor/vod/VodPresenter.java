package com.zzz.monitor.vod;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zzz.monitor.api.HttpClient;
import com.zzz.monitor.bean.Response;
import com.zzz.monitor.bean.VodUrl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class VodPresenter extends AppCompatActivity implements VodContract.Presenter {
    private VodContract.View mView;
    private Context mContext;
    private Timer mTimer;

    public VodPresenter(Context context, VodContract.View view) {
        this.mContext = context;
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void playVod(final String sn, final String uniqueId, long timestamp) {
        HttpClient.getInstance()
                .playVod(sn, uniqueId, timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VodUrl>() {
                    @Override
                    public void onStart() {
                        mView.showProgress(true);
                    }

                    @Override
                    public void onCompleted() {
                        mView.showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProgress(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            try {
                                String errorBody = httpException.response().errorBody().string();
                                Response response = new Gson().fromJson(errorBody, Response.class);
                                mView.toast(response.getMessage());
                            } catch (IOException ex) {
                                mView.toast("无法连接服务器，请检查网络");
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(VodUrl vodUrl) {
                        cancelPlayTimer();
                        initPlayTimer(sn, uniqueId);
                        mView.vodStart(vodUrl.getDefaultX());
                    }
                });
    }

    @Override
    public void keepVod(String sn, String uniqueId) {
        HttpClient.getInstance()
                .keepVod(sn, uniqueId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Response response) {
                    }
                });
    }

    private void initPlayTimer(final String sn, final String uniqueId) {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepVod(sn, uniqueId);
            }
        }, 0, 5_000);
    }

    public void cancelPlayTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
