package com.zzz.monitor.home;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zzz.monitor.adapter.CameraAdapter;
import com.zzz.monitor.api.HttpClient;
import com.zzz.monitor.api.HttpClientOur;
import com.zzz.monitor.bean.Camera;
import com.zzz.monitor.bean.DeviceListBean;
import com.zzz.monitor.bean.Response;
import com.zzz.monitor.bean.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zzz.monitor.api.HttpClient.clientId;
import static com.zzz.monitor.api.HttpClient.clientSecret;


public class HomePresenter extends AppCompatActivity implements HomeContract.Presenter {
    private HomeContract.View mView;
    private List<Camera> mCameras = new ArrayList<>();
    private CameraAdapter mCameraAdapter;

    public HomePresenter(Context context, HomeContract.View view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    public void getToken() {
        HttpClient.getInstance()
                .token(clientId, clientSecret)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Token>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwipe();
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
                    public void onNext(Token token) {
                        HttpClient.token = token.getAuthorization();
                        mView.init();
                    }
                });
    }

    public void getDevices(Context context,final int page, int isOnline, String keyword) {
        HttpClientOur.getInstance(context)
                .devices(page, isOnline, keyword,2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeviceListBean>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideSwipe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwipe();
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
                    public void onNext(DeviceListBean deviceListBean) {
                        if (deviceListBean!=null){
                            HttpClient.token = deviceListBean.getAuthorization();
//                            List<Camera>listResponse=deviceListBean.getObjects();
                            if (deviceListBean.getTotal() < 1) {
                                mView.showEmptyMsg();
                                return;
                            } else {
                                mView.hideEmptyMsg();
                            }

                            if (deviceListBean.getObjects().size() > 0) {
                                mView.lock(false);
                            }

                            if (page == 1) {
                                mCameras.clear();
                            }

                            mCameras.addAll(deviceListBean.getObjects());
                            if (mCameraAdapter == null) {
                                mCameraAdapter = new CameraAdapter(mCameras);
                                mView.setAdapter(mCameraAdapter);
                            } else {
                                android.util.Log.e("===debug===", "Change: " + mCameras.size());
                                mCameraAdapter.notifyDataSetChanged();
                            }
                        }
                        }
                });
    }
}
