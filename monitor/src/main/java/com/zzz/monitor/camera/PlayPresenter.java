package com.zzz.monitor.camera;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zzz.monitor.api.HttpClient;
import com.zzz.monitor.bean.LiveUrl;
import com.zzz.monitor.bean.Response;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PlayPresenter implements PlayContract.Presenter {
    private final String TAG = "DEBUG - " + this.getClass().getName();
    private PlayContract.View mView;
    private Context mContext;
    private Timer mTimer;
    private String mSn;
    private boolean mPlayable = true;

    public PlayPresenter(Context context, PlayContract.View view) {
        this.mContext = context;
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void play(final String sn) {
        mSn = sn;

        HttpClient.getInstance()
                .live(sn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LiveUrl>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast("获取播放地址失败");
                        if (mPlayable) {
                            play(sn);
                        }
                    }

                    @Override
                    public void onNext(LiveUrl liveUrl) {
                        if (TextUtils.isEmpty(liveUrl.getRtmp())){
                            mView.toast("获取播放地址失败");
                            if (mPlayable) {
                                play(sn);
                            }
                        }else {
                            mView.liveStart(liveUrl.getRtmp());
                            initPlayTimer();
                            mView.toast("获取播放地址成功");
                        }
                    }
                });
    }

    @Override
    public void keepPlay(String sn) {
        HttpClient.getInstance()
                .live(sn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LiveUrl>() {
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
                    public void onNext(LiveUrl liveUrl) {
                    }
                });
    }

    private void initPlayTimer() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepPlay(mSn);
            }
        }, 0, 5_000);
    }

    public void cancelPlayTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    public void takePhoto() {
        HttpClient.getInstance()
                .takePhoto(mSn)
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
                            mView.toast("网络错误");
                        }
                    }

                    @Override
                    public void onNext(Response response) {
                        mView.toast(response.getMessage());
                    }
                });
    }

    @Override
    public void ptz(String direction) {
        HttpClient.getInstance()
                .ptz(mSn, direction)
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
                            mView.toast("网络错误");
                        }
                    }

                    @Override
                    public void onNext(Response response) {
                        mView.toast(response.getMessage());
                    }
                });
    }

    @Override
    public void sendAudio(String audio) {
        HttpClient.getInstance()
                .sendAudio(mSn, audio)
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

    @Override
    public void cancelPlay() {
        mPlayable = false;
    }
}
