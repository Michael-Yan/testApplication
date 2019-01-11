package com.zzz.monitor.vod;


import com.zzz.monitor.base.BasePresenter;
import com.zzz.monitor.base.BaseView;

public interface VodContract {
    interface Presenter extends BasePresenter {
        void playVod(String sn, String uniqueId, long timestamp);

        void keepVod(String sn, String uniqueId);
    }

    interface View extends BaseView<Presenter> {
        void toast(String text);

        void showProgress(boolean isShow);

        void vodStart(String url);
    }
}
