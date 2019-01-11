package com.zzz.monitor.camera;

import android.content.Context;

import com.google.gson.Gson;
import com.zzz.monitor.adapter.GalleryAdapter;
import com.zzz.monitor.api.HttpClient;
import com.zzz.monitor.bean.ListResponse;
import com.zzz.monitor.bean.Response;
import com.zzz.monitor.bean.Snapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GalleryPresenter implements GalleryContract.Presenter {
    private final String TAG = "DEBUG - " + this.getClass().getName();
    private GalleryContract.View mView;
    private Context mContext;
    private GalleryAdapter mSnapshotAdapter;
    private List<Object> mList = new ArrayList<>();
    private List<Snapshot> mSnapshots = new ArrayList<>();

    public GalleryPresenter(Context context, GalleryContract.View view) {
        this.mContext = context;
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void getGallery(String sn, int page, final String start, String end) {
        HttpClient.getInstance()
                .snapshots(sn, page, start, end)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ListResponse<Snapshot>>() {
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
                            mView.toast("获取相册数据失败");
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ListResponse<Snapshot> listResponse) {
                        if (listResponse.getTotal() < 1) {
                            mView.showEmptyMsg();
                        } else {
                            mView.hideEmptyMsg();
                        }

                        if (listResponse.getTotal() > 0) {
                            mView.lock(false);
                        }

                        mSnapshots.addAll(listResponse.getObjects());
                        mList.clear();

                        for (Snapshot snapshot : mSnapshots) {
                            String sdfDate = snapshot.getSdfDate();
                            if (null != sdfDate && !mList.contains(sdfDate)) {
                                mList.add(sdfDate);
                            }
                            mList.add(snapshot);
                        }

                        if (mSnapshotAdapter == null) {
                            mSnapshotAdapter = new GalleryAdapter(mList);
                            mView.setAdapter(mSnapshotAdapter);
                        } else {
                            mSnapshotAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
